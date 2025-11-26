package controllers

import algoQuePedir.App
import algoQuePedir.domain.*
import algoQuePedir.dto.UserUpdateDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import local.Local
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import kotlin.test.assertEquals

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@DisplayName("Actualizacion del usuario")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var repoUsuario: repoUsuario

    @Autowired
    lateinit var repoLocal: repoLocal

    lateinit var mockLocal: Local
    lateinit var mockPlato: Plato

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun init() {
        repoUsuario.coleccion.clear()
        repoLocal.coleccion.clear()

        val usuario = Usuario(
            datos = DatosPersonales(
                nombre = "asd123",
                apellido = "dsa321",
                username = "guti12",
                password = "123abc"
            ),
            fechaNacimiento = "01012000",
            ubicacion = Direccion("Calle Falsa", 123.0, 1.0, 1)
        ).apply { id = 1 }

        repoUsuario.create(usuario)

        mockLocal = Local(
            nombreLocal = "Pizza Ya",
            direccion = Direccion("Cabildo", 20.0, 3.0, 100),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
            menu = mutableListOf()
        ).apply {
            adminUsername = usuario.datos.username
            imgURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKtCWoQOzlUBun9VGIO2HG1KSoofc2emPLwg&s"
            id = 1
        }

        mockPlato = Plato(
            nombre = "Pancho",
            descripcion = "pan con salchicha",
            ingredientes = mutableListOf(),
            local = mockLocal,
            descuento = 0.1,
            fechaLanzamiento = LocalDateTime.now().minusDays(35), // para que no sea "nuevo"
            esDeAutor = true,
            porcentajeRegalia = 0.03,
            valorBase = 50.0
        ).apply {
            cantidad = 1
            imagen = "/src/lib/assets/pancho.jpg"
            id = 1
        }

        mockLocal.menu.add(mockPlato)
        repoLocal.create(mockLocal)
    }

    @AfterEach
    fun removeLocal() {
        repoLocal.delete(mockLocal)
    }

    @Test
    @DisplayName("PUT /user/1 actualiza y despues GET /user/1 devuelve los datos actualizados")
    fun `PUT actualiza y GET devuelve los datos actualizados`() {
        val carne = Ingrediente(
            "CARNE",
            100.5,
            GrupoAlimenticio.PROTEINAS,
            true
        )

        val updatedUser = UserUpdateDTO(
            nombre = "Gustavo",
            apellido = "Muinos",
            direccion = "Av siempre viva",
            altura = 123,
            latitud = 2.0,
            longitud = 3.0,
            condicion = Veganos,
            ingredientesEvitar = mutableListOf("carne"),
            ingredientesPreferidos = mutableListOf(),
            restosFavoritos = mutableListOf(),
            palabrasMarketing = mutableListOf(),
            distanciaMax = 5.00
        )

        // --- PUT ---
        mockMvc.perform(
            put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
        )
            .andExpect(status().isOk)

        // --- GET (datos actualizados) ---
        mockMvc.perform(get("/user/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.datos.nombre").value("Gustavo"))
            .andExpect(jsonPath("$.datos.apellido").value("Muinos"))
            .andExpect(jsonPath("$.datos.username").value("guti12"))
            .andExpect(jsonPath("$.datos.password").value("123abc"))
            .andExpect(jsonPath("$.ubicacion.calle").value("Av siempre viva"))
            .andExpect(jsonPath("$.ubicacion.altura").value(123))
            .andExpect(jsonPath("$.ubicacion.coordenadaX").value(2.0))
            .andExpect(jsonPath("$.ubicacion.coordenadaY").value(3.0))
            .andExpect(jsonPath("$.condicion.tipo").value("Veganos"))
    }

    @Test
    @DisplayName("POST /confirmarPedido/1/1 agrega un pedido al usuario y al local")
    fun `POST confirmar pedido actualiza correctamente`() {
        var mvckResult = mockMvc.perform(get("/userPedido/1"))
            .andExpect(status().isOk)
            .andReturn()

        var jsonResponse = mvckResult.response.contentAsString
        val cantidadPedidosOld: Int = JsonPath.read<List<Any>>(jsonResponse, "$.pedidos").size

        val pedidoBody = """
            {
              "id": 999,
              "estado": "PENDIENTE",
              "nombreLocal": "Pizza Ya",
              "valoracion": 4.2,
              "distanciaALocal": 3.5,
              "fecha": "01-01-2025",
              "fotoLocal": "/src/lib/assets/fotom.jpg",
              "items": 1,
              "total": 1463.7,
              "metodoPago": "TRANSFERENCIA",
              "platos": [
                {
                  "id": 1,
                  "nombrePlato": "Pancho",
                  "cantidad": 1,
                  "costo": 119.625,
                  "imagenPlato": "/src/lib/assets/pancho.jpg"
                }
              ],
              "costeEnvio": 164.0,
              "adicionalPorMedio": 1.05
            }
        """.trimIndent()

        val mockResponseLocal = mockMvc.perform(get("/locales"))
            .andExpect(status().isOk)
            .andReturn()

        val jsonLocales = mockResponseLocal.response.contentAsString
        val localID: Int = JsonPath.read(jsonLocales, "$[0].id")

        mockMvc.perform(
            post("/confirmarPedido/${localID}/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pedidoBody)
        )
            .andExpect(status().isOk)

        mvckResult = mockMvc.perform(get("/userPedido/1"))
            .andExpect(status().isOk)
            .andReturn()

        jsonResponse = mvckResult.response.contentAsString
        val cantidadPedidosNew: Int = JsonPath.read<List<Any>>(jsonResponse, "$.pedidos").size
        assertEquals(cantidadPedidosOld + 1, cantidadPedidosNew)
    }
}

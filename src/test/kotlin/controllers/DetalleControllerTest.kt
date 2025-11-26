package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.*
import local.Local
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de detalle de pedido")
class DetalleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repoLocal: repoLocal

    @Autowired
    private lateinit var repoUsuario: repoUsuario

    @Autowired
    private lateinit var repoPlato: repoPlato

    private lateinit var testUser: Usuario
    private lateinit var testLocal: Local
    private lateinit var pancho: Plato
    private lateinit var burger: Plato
    private lateinit var testPedido: Pedido

    @BeforeEach
    fun init() {
        repoLocal.coleccion.clear()
        repoUsuario.coleccion.clear()
        repoPlato.coleccion.clear()

        testUser = Usuario(
            datos = DatosPersonales(
                nombre = "Sofia",
                apellido = "Miller",
                username = "smiller2005",
                password = "123"
            ),
            fechaNacimiento = "01012010",
            ubicacion = Direccion("Av siempre viva", 555.0, 100.0, 100)
        )
        repoUsuario.create(testUser)

        testLocal = Local(
            nombreLocal = "Pizza Ya",
            direccion = Direccion("Cabildo", 20.0, 3.0, 100),
            porcentajeBeneficio = 0.2,
        ).apply {
            adminUsername = "testAdmin"
            imgURL = "/src/lib/assets/fotom.jpg"
            listaPedidos = mutableListOf()
        }
        repoLocal.create(testLocal)

        pancho = Plato(
            nombre = "Pancho",
            descripcion = "pan con salchicha",
            ingredientes = mutableListOf(),
            local = testLocal,
            descuento = 0.1,
            fechaLanzamiento = LocalDateTime.now().minusDays(40),
            esDeAutor = true,
            porcentajeRegalia = 0.03,
            valorBase = 119.625
        ).apply {
            cantidad = 1
            imagen = "/src/lib/assets/pancho.jpg"
        }

        burger = Plato(
            nombre = "Hamburguesa",
            descripcion = "Hamburguesa clásica con queso",
            ingredientes = mutableListOf(),
            local = testLocal,
            descuento = 0.0,
            fechaLanzamiento = LocalDateTime.now().minusDays(40),
            esDeAutor = false,
            porcentajeRegalia = 0.0,
            valorBase = 1110.375
        ).apply {
            cantidad = 1
            imagen = "/src/lib/assets/burger.jpg"
        }

        repoPlato.create(pancho)
        repoPlato.create(burger)

        // Crear pedido
        testPedido = Pedido(
            cliente = testUser,
            local = testLocal,
            platos = mutableListOf(pancho, burger),
            medioDePago = MedioDePago.TRANSFERENCIA,
            estadoDelPedido = EstadoDelPedido.PENDIENTE,
            momentoDeOrden = LocalDateTime.now()
        ).apply {
            id = 1
        }

        testLocal.listaPedidos.add(testPedido)
    }

    @Test
    fun `GET detalle del pedido retorna los datos correctos`() {
        mockMvc.perform(
            get("/pedidos/{id}", 1)
                .param("user", "testAdmin")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
            .andExpect(jsonPath("$.usuario").value("smiller2005"))
            .andExpect(jsonPath("$.platos[0].nombrePlato").value("Pancho"))
            .andExpect(jsonPath("$.platos[1].nombrePlato").value("Hamburguesa"))
            .andExpect(jsonPath("$.metodoPago").value("TRANSFERENCIA"))
            .andExpect(jsonPath("$.total").exists())
    }

    @Test
    fun `GET detalle de pedido inexistente retorna 404`() {
        mockMvc.perform(
            get("/pedidos/{id}", 999)
                .param("user", "testAdmin")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string("Pedido con id 999 no encontrado para el local testAdmin"))
    }

    @Test
    fun `GET detalle de pedido con admin inexistente retorna 400`() {
        mockMvc.perform(
            get("/pedidos/{id}", 1)
                .param("user", "otroAdmin")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se encontro un local que pertenezca a otroAdmin"))
    }
}

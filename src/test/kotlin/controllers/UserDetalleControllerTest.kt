package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.*
import algoQuePedir.dto.toUserDTO
import com.fasterxml.jackson.databind.ObjectMapper
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
@DisplayName("Dado un controller de detalle de pedido del usuario")
class UserPedidoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repoLocal: repoLocal

    @Autowired
    private lateinit var repoUsuario: repoUsuario

    @Autowired
    private lateinit var repoPlato: repoPlato

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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

        algoQuePedir.domain.repoLocal.coleccion = repoLocal.coleccion
        // Crear usuario
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

        // Crear local
        testLocal = Local(
            nombreLocal = "Pizza Ya",
            direccion = Direccion("Cabildo", 20.0, 3.0, 100),
            porcentajeBeneficio = 0.2,
        ).apply {
            adminUsername = "testAdmin"
            imgURL = "/src/lib/assets/fotom.jpg"
            listaPedidos = mutableListOf()
            puntajes = mutableListOf(5, 4)
        }
        repoLocal.create(testLocal)

        // Crear platos
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

        val segundoPedido = Pedido(
            cliente = testUser,
            local = testLocal,
            platos = mutableListOf(pancho),
            medioDePago = MedioDePago.QR,
            estadoDelPedido = EstadoDelPedido.PENDIENTE,
            momentoDeOrden = LocalDateTime.now()
        ).apply { id = 2 }

        // Asociar pedido al usuario y al local
        testLocal.listaPedidos.add(testPedido)
        testUser.listaPedidos.add(testPedido)
        testLocal.listaPedidos.addAll(listOf(testPedido, segundoPedido))
        testUser.listaPedidos.addAll(listOf(testPedido, segundoPedido))
    }

    //GetAll request
    @Test
    fun `GET lista de pedidos de un usuario retorna correctamente`() {
        mockMvc.perform(
            get("/user/pedidos")
                .param("user", "smiller2005")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].nombreLocal").value("Pizza Ya"))
            .andExpect(jsonPath("$[0].platos[0].nombrePlato").value("Pancho"))
            .andExpect(jsonPath("$[0].platos[1].nombrePlato").value("Hamburguesa"))
            .andExpect(jsonPath("$[1].platos[0].nombrePlato").value("Pancho"))
    }

    @Test
    fun `GET lista de pedidos con usuario inexistente retorna 400`() {
        mockMvc.perform(
            get("/user/pedidos")
                .param("user", "random")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se pudo encontrar al usuario random"))
    }

    //GetById request
    @Test
    fun `GET detalle del pedido retorna los datos correctos`() {
        mockMvc.perform(
            get("/user/pedidos/{id}/Pizza Ya", 1)
                .param("user", "smiller2005")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
            .andExpect(jsonPath("$.nombreLocal").value("Pizza Ya"))
            .andExpect(jsonPath("$.platos[0].nombrePlato").value("Pancho"))
            .andExpect(jsonPath("$.platos[1].nombrePlato").value("Hamburguesa"))
            .andExpect(jsonPath("$.metodoPago").value("TRANSFERENCIA"))
            .andExpect(jsonPath("$.total").exists())
    }

    @Test
    fun `GET detalle de pedido inexistente retorna 404`() {
        mockMvc.perform(
            get("/user/pedidos/{id}/Pizza Ya", 999)
                .param("user", "smiller2005")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(("Pedido con ID: 999 no encontrado para el usuario smiller2005")))
    }

    @Test
    fun `GET detalle de pedido con usuario inexistente retorna 400`() {
        mockMvc.perform(
            get("/user/pedidos/{id}/Pizza Ya", 1)
                .param("user", "random")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se pudo encontrar al usuario random"))
    }

    //PUT request
    @Test
    fun `PUT detalle de pedido de Pendiente a Cancelado`() {
        val dto = testPedido.toUserDTO()

        mockMvc.perform(
            put("/user/pedidos/cancelar")
                .param("user", "smiller2005")
                .contentType(MediaType.APPLICATION_JSON)
                //serializo el DTO a JSON
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].estado").value("CANCELADO"))
            .andExpect(jsonPath("$[0].nombreLocal").value("Pizza Ya"))
            .andExpect(jsonPath("$[0].platos[0].nombrePlato").value("Pancho"))
            .andExpect(jsonPath("$[0].platos[1].nombrePlato").value("Hamburguesa"))
            .andExpect(jsonPath("$[0].metodoPago").value("TRANSFERENCIA"))
            .andExpect(jsonPath("$[0].total").exists())
    }

    @Test
    fun `PUT cancelar pedido con usuario inexistente retorna 400`() {
        val dto = testPedido.toUserDTO()

        mockMvc.perform(
            put("/user/pedidos/cancelar")
                .param("user", "random")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se pudo encontrar al usuario random"))
    }

    @Test
    fun `PUT cancelar pedido ya entregado o cancelado retorna 400`() {
        // cambiamos el estado del pedido
        testPedido.estadoDelPedido = EstadoDelPedido.CANCELADO
        val dto = testPedido.toUserDTO()

        mockMvc.perform(
            put("/user/pedidos/cancelar")
                .param("user", "smiller2005")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se puede cancelar un pedido ya entregado o cancelado"))
    }

    @Test
    fun `PUT cancelar pedido inexistente retorna 400`() {
        val dto = testPedido.toUserDTO().copy(id = 999) // simulo id que no existe

        mockMvc.perform(
            put("/user/pedidos/cancelar")
                .param("user", "smiller2005")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Pedido con ID: 999 no encontrado para el usuario smiller2005"))
    }
}

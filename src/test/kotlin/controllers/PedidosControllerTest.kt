package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.*
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoUsuario
import local.Local
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de Pedidos")
class PedidosControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repoLocal: repoLocal

    @Autowired
    private lateinit var repoUsuario: repoUsuario
    private lateinit var testUser: Usuario
    private lateinit var testLocal: Local
    private lateinit var testPedidoPendiente: Pedido
    private lateinit var testPedidoEntregado: Pedido

    @BeforeEach
    fun init() {
        repoLocal.coleccion.clear()
        repoUsuario.coleccion.clear()

        testUser = Usuario(
            datos = DatosPersonales(
                nombre = "Cliente",
                apellido = "Test",
                username = "clientetest",
                password = "123"
            ),
            fechaNacimiento = "01012010",
            ubicacion = Direccion("Falsa 123", 123.0, 1.0, 100)
        )
        repoUsuario.create(testUser)

        testLocal = Local(
            nombreLocal = "Local de Prueba",
            direccion = Direccion("Otra Calle 456", 456.0, 1.0, 100),
            porcentajeBeneficio = 0.2,
        ).apply {
            adminUsername = "testAdmin"
            imgURL = "url.com/img.png"
            listaPedidos = mutableListOf()
        }

        testPedidoPendiente = Pedido(
            cliente = testUser,
            local = testLocal,
            medioDePago = MedioDePago.EFECTIVO,
            estadoDelPedido = EstadoDelPedido.PENDIENTE,
            momentoDeOrden = LocalDateTime.now()
        ).apply { id = 1 }

        testPedidoEntregado = Pedido(
            cliente = testUser,
            local = testLocal,
            medioDePago = MedioDePago.TRANSFERENCIA,
            estadoDelPedido = EstadoDelPedido.ENTREGADO,
            momentoDeOrden = LocalDateTime.now().minusHours(1)
        ).apply { id = 2 }

        testLocal.listaPedidos.add(testPedidoPendiente)
        testLocal.listaPedidos.add(testPedidoEntregado)

        repoLocal.create(testLocal)
    }

    // --- Tests para GET /pedidos ---

    @Test
    fun `GET pedidos sin filtro retorna todos los pedidos del admin`() {
        mockMvc.perform(
            get("/pedidos")
                .param("user", "testAdmin")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
            .andExpect(jsonPath("$[1].estado").value("ENTREGADO"))
    }

    @Test
    fun `GET pedidos con filtro PENDIENTE retorna solo pedidos pendientes`() {
        mockMvc.perform(
            get("/pedidos")
                .param("user", "testAdmin")
                .param("estado", "PENDIENTE")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(testPedidoPendiente.id))
            .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
    }

    @Test
    fun `GET pedidos con filtro que no devuelve resultados`() {
        mockMvc.perform(
            get("/pedidos")
                .param("user", "testAdmin")
                .param("estado", "CANCELADO")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `GET pedidos con un admin(username) incorrecto retorna error`() {
        mockMvc.perform(
            get("/pedidos")
                .param("user", "userIncorrecto")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se encontro un local que pertenezca a userIncorrecto"))
    }

    // --- Tests para GET /pedidos/{id} ---

    @Test
    fun `GET pedidos por ID retorna el pedido correcto`() {
        val pedidoId = testPedidoPendiente.id!!

        mockMvc.perform(
            get("/pedidos/$pedidoId")
                .param("user", "testAdmin")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(pedidoId))
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
    }

    @Test
    fun `GET pedidos por ID que no existe retorna 404 Not Found`() {
        mockMvc.perform(
            get("/pedidos/999")
                .param("user", "testAdmin")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string("Pedido con id 999 no encontrado para el local testAdmin"))
    }

    @Test
    fun `GET pedidos por ID de otro local (admin incorrecto) retorna 400`() {
        val pedidoId = testPedidoPendiente.id!!

        mockMvc.perform(
            get("/pedidos/$pedidoId")
                .param("user", "otroAdmin")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No se encontro un local que pertenezca a otroAdmin"))
    }
}
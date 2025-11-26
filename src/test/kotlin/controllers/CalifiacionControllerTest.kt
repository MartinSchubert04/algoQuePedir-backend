package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.*
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.CalificacionDTO
import algoQuePedir.dto.UserUpdateDTO
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime


@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de Pedidos")
class CalifiacionControllerTest {

    private val objectMapper = ObjectMapper()

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
        ).apply { id = 1 }
        repoUsuario.create(testUser)

        testLocal = Local(
            nombreLocal = "Local de Prueba",
            direccion = Direccion("Otra Calle 456", 456.0, 1.0, 100),
            porcentajeBeneficio = 0.2,
        ).apply {
            adminUsername = "testAdmin"
            imgURL = "url.com/img.png"
            listaPedidos = mutableListOf()
            id = 1
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

    @Test
    @DisplayName("GET trae los locales a calificar con el pedido entregado")

    fun `GET devuelve lista de locales para calificar`() {

        mockMvc.perform(
            get("/calificaciones/1")
                .contentType("application/json")
            .content(
                """
                    {
                        "id":1
                        "nombreLocal": "Local de Prueba",
                        "imagen": url.com/img.png,
                        "distancia": "CEREALES_TUBERCULOS",
                        "promedio": 0
                    }
                    """
            )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @DisplayName("PUT actualiza puntaje del local")
    fun `PATCH actualiza puntaje local`() {
        val puntaje = CalificacionDTO(1,4)
        // --- PUT ---
        mockMvc.perform(
            patch("/calificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(puntaje))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
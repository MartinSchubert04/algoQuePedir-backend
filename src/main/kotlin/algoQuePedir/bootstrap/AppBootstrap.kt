package algoQuePedir.bootstrap

import algoQuePedir.domain.*
import algoQuePedir.domain.DatosPersonales
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.Ingrediente
import local.Local
import local.adminLocal
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import algoQuePedir.domain.Plato
import java.time.LocalDateTime

@Service
class AppBootstrap(
    val usuarioRepository: repoUsuario,
    val adminLocalRepository: repoAdminLocal,
    val ingredienteRepository: repoIngrediente,
    val platoRepository: repoPlato,
    val localRepository: repoLocal,
    ) : InitializingBean {

    lateinit var adminLocal: adminLocal
    lateinit var adminLocal2: adminLocal

    lateinit var pizzasYa: Local
    lateinit var saboresExpress: Local
    lateinit var mcDonalds: Local


    lateinit var carne: Ingrediente
    lateinit var pasto: Ingrediente

    // PLATOS ------------------
    lateinit var pancho: Plato
    lateinit var empanada: Plato
    lateinit var hamburguesa: Plato
    lateinit var pizza: Plato

    lateinit var cliente1: Usuario
    lateinit var usuarioFalopa: Usuario

    override fun afterPropertiesSet() {
        this.initializeUser()
        this.initializeAdminUser()
        this.initializeIngredients()
        this.initializeLocales()
        this.initializePlatos()
        this.finalizeUsuariosMock()

        repoLocal.coleccion = localRepository.coleccion
        repoUsuario.coleccion = usuarioRepository.coleccion
    }

    // --- Step 1 & 5 Helper Functions ---

    fun initializeUser() {
        cliente1 = Usuario (
            datos = DatosPersonales(
                nombre = "Agustin",
                apellido = "Gutierrez",
                username = "123",
                password = "123"
            ),
            fechaNacimiento = "01012010",
            ubicacion = Direccion("Cabildo", 2.0, 1.0,3)
        )
        usuarioRepository.create(cliente1)

        usuarioFalopa = Usuario(
            datos = DatosPersonales(
                nombre = "Falopa",
                apellido = "Perez",
                username = "falopa200",
                password = "123"
            ),
            fechaNacimiento = "01012000",
            ubicacion = Direccion("Falsa 999", 999.0, 1.0, 100)
        )
        usuarioRepository.create(usuarioFalopa)

    }

    fun initializeAdminUser() {
        adminLocal = adminLocal("123", "123")
        adminLocal2 = adminLocal("admin", "admin")
        adminLocalRepository.apply {
            create(adminLocal)
            create(adminLocal2)
        } // Use the injected name
    }

    fun initializeIngredients() {
        carne = Ingrediente(
            nombre = "CARNE",
            costoMercado = 100.5,
            GrupoAlimenticio.PROTEINAS,
            esDeOrigenAnimal = true
        )
        pasto = Ingrediente(
            nombre = "PASTO",
            costoMercado = 200.1,
            GrupoAlimenticio.FRUTAS_VERDURAS,
            esDeOrigenAnimal = false
        )
        ingredienteRepository.create(carne)
        ingredienteRepository.create(pasto)

        cliente1.agregarPreferido(carne)
        cliente1.agregarEvitar(pasto)
    }


    fun initializeLocales() {
        pizzasYa = Local(
            nombreLocal = "Pizzas Ya",
            direccion = Direccion("Av. Cordoba", 20.0, 3.0, 531),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
        ).apply {
            adminUsername = adminLocal.username
            imgURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKtCWoQOzlUBun9VGIO2HG1KSoofc2emPLwg&s"
            puntajes = mutableListOf(3,3,2,3,2,1,1,5,5,4)
            inbox = Inbox().apply { mensajes = mutableListOf(
                Mensaje( asunto = "Buena comida", contenido = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Itaque sint illum impedit labore, soluta, necessitatibus debitis, natus excepturi", leido = true),
                Mensaje( asunto = "Demora", contenido = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Itaque sint illum impedit labore, soluta, necessitatibus debitis, natus excepturi", leido = true),
                Mensaje( asunto = "Devolucion", contenido = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Itaque sint illum impedit labore, soluta, necessitatibus debitis, natus excepturi", leido = true),
                Mensaje( asunto = "Un asco", contenido = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Itaque sint illum impedit labore, soluta, necessitatibus debitis, natus excepturi", leido = true),
            ) }
        }

        saboresExpress = Local(
            nombreLocal = "Sabores Express",
            direccion = Direccion("Av. Cordoba", 50.0, 33.0, 6756),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
        ).apply {
            adminUsername = ""
            imgURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQOuTiJzxuVJp0OlbgGuJscR6VNUIyUP7i1zg&s"
            puntajes = mutableListOf(3,3,2,3,2,1,1,5,5,4)
            inbox = Inbox().apply { mensajes = mutableListOf(
                Mensaje( asunto = "reseña", contenido = "llego rapido", leido = true),
                Mensaje( asunto = "reseña2", contenido = "comida maso", leido = true),
                Mensaje( asunto = "reseña3", contenido = "comida fea", leido = true),
                Mensaje( asunto = "reseña4", contenido = "llego rapido", leido = true),
            ) }
        }

        mcDonalds = Local(
            nombreLocal = "McDonald's",
            direccion = Direccion("Av. Cordoba", 10.0, 31.0, 3100),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
        ).apply {
            adminUsername = adminLocal2.username
            imgURL = "https://cdn-club.lavoz.com.ar/marcas/O93260847.webp"
            puntajes = mutableListOf(3,2,2,3,2,1,5,5)
            inbox = Inbox().apply { mensajes = mutableListOf(
                Mensaje( asunto = "reseña", contenido = "comida muy rica", leido = true),
                Mensaje( asunto = "reseña2", contenido = "comida maso", leido = true),
                Mensaje( asunto = "reseña3", contenido = "comida fea", leido = true),
                Mensaje( asunto = "reseña4", contenido = "comida muy rica", leido = true),
            ) }
        }

        // Agregamos locales preferidos a un usuario
        cliente1.localesPreferidos = mutableListOf(mcDonalds, saboresExpress)

        localRepository.apply {
            create(pizzasYa)
            create(saboresExpress)
            create(mcDonalds)
        }
        adminLocal.local = pizzasYa
    }


    fun finalizeUsuariosMock() {
        val pedidos = createPedidosForLocal()

        val clienteSofia = usuarioRepository.buscar("123").firstOrNull()
            ?: pedidos.first { it.cliente.datos.username == "123" }.cliente.also {
                usuarioRepository.create(it)
            }

        val clienteFalopa = usuarioRepository.buscar("falopa200").firstOrNull()
            ?: pedidos.first { it.cliente.datos.username == "falopa200" }.cliente.also {
                usuarioRepository.create(it)
            }

        clienteSofia.listaPedidos = pedidos
            .filter { it.cliente.datos.username == clienteSofia.datos.username }
            .toMutableList()

        clienteFalopa.listaPedidos = pedidos
            .filter { it.cliente.datos.username == clienteFalopa.datos.username }
            .toMutableList()
    }


    fun initializePlatos() = platoRepository.apply {

        pancho = Plato(
            nombre = "Pancho",
            descripcion = "Carne mixta, con pan artesanal, 2 aderezos a eleccion",
            ingredientes = mutableListOf(carne),
            local = saboresExpress,
            descuento = 0.1,
            fechaLanzamiento = LocalDateTime.now().minusDays(5), // es para que el plato siempre salga hace 5 dias,
            esDeAutor = true,
            porcentajeRegalia = 0.03,
            valorBase = 4000.0
        ).apply {
            cantidad = 1
            imagen = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRWTsM9_KKdaryNYbqgb0kmKWpRpVr3na-Niw&s"
        }

        empanada = Plato(
            nombre = "Empanada",
            descripcion = "Empanada gusto cheeseburguer, fill text fill text fill text fill text fill text fill text",
            ingredientes = mutableListOf(carne),
            local = saboresExpress,
            descuento = 0.1,
            fechaLanzamiento = LocalDateTime.now().minusDays(5), // es para que el plato siempre salga hace 5 dias,
            esDeAutor = true,
            porcentajeRegalia = 0.03,
            valorBase = 4000.0
        ).apply {
            cantidad = 1
            imagen = "https://http2.mlstatic.com/D_837617-MLA81772889380_012025-O.jpg"
        }

        hamburguesa = Plato(
            nombre = "Hamburguesa",
            descripcion = "Whopper doble. Hamburguesa clásica con queso, tomate y lechuga.",
            ingredientes = mutableListOf(carne),
            local = mcDonalds,
            descuento = 0.0,
            fechaLanzamiento = LocalDateTime.now().minusDays(5),
            esDeAutor = false,
            porcentajeRegalia = 0.0,
            valorBase = 13000.0,
        ).apply {
            cantidad = 1
            imagen = "https://www.df.cl/noticias/site/artic/20230522/imag/foto_0000000220230522095658/AI-WHOPPER_2.jpeg"
        }

        pizza = Plato(
            nombre = "Pizza Margarita",
            descripcion = "Pizza estilo italiano, queso de buffala y salsa marinara",
            ingredientes = mutableListOf(carne),
            local = pizzasYa,
            descuento = 0.0,
            fechaLanzamiento = LocalDateTime.now().minusDays(5),
            esDeAutor = false,
            porcentajeRegalia = 0.0,
            valorBase = 25000.0,
        ).apply {
            cantidad = 1
            imagen = "https://wp-cdn.typhur.com/wp-content/uploads/2025/01/homemade-pizza-in-air-fryer.jpg"
        }

        create(pancho)
        create(hamburguesa)
        create(pizza)
        create(empanada)

        saboresExpress.apply {
            menu = mutableListOf(empanada)
        }

        mcDonalds.apply {
            menu = mutableListOf(hamburguesa)

        }
        pizzasYa.apply {
            menu = mutableListOf(pizza, pancho)
        }

        pizzasYa.listaPedidos = createPedidosForLocal()
        mcDonalds.listaPedidos = createPedidosForLocal()
        saboresExpress.listaPedidos = createPedidosForLocal()
    }

    fun createPedidosForLocal(): MutableList<Pedido> {
        val pedidos = mutableListOf(
                Pedido(
                    cliente = cliente1,
                    local = pizzasYa,
                    platos = mutableListOf(pancho, hamburguesa),
                    medioDePago = MedioDePago.TRANSFERENCIA, // Pago con tarjeta de crédito
                    estadoDelPedido = EstadoDelPedido.PENDIENTE
                ).apply { id = -3 },
        Pedido(
            cliente = usuarioFalopa,
            local = pizzasYa,
            platos = mutableListOf(pancho),
            medioDePago = MedioDePago.EFECTIVO, // Pago en efectivo
            estadoDelPedido = EstadoDelPedido.ENTREGADO,
            ).apply { id = -2 },
        Pedido(
            cliente = cliente1,
            local = pizzasYa,
            platos = mutableListOf(pancho),
            medioDePago = MedioDePago.QR, // Pago con QR
            estadoDelPedido = EstadoDelPedido.CANCELADO
        ).apply { id = -1 },

        Pedido(
        cliente = cliente1,
            local = pizzasYa,
            platos = mutableListOf(pancho, hamburguesa),
            medioDePago = MedioDePago.TRANSFERENCIA,
            estadoDelPedido = EstadoDelPedido.ENTREGADO
        ).apply { id = -4 },
            Pedido(
                cliente = cliente1,
                local = mcDonalds,
                platos = mutableListOf(pancho, hamburguesa),
                medioDePago = MedioDePago.TRANSFERENCIA,
                estadoDelPedido = EstadoDelPedido.ENTREGADO
            ).apply { id = -5 },
        )
        return pedidos
    }
}
import algoQuePedir.domain.CambiarAVeganoObserver
import algoQuePedir.domain.CondicionY
import algoQuePedir.domain.Conservador
import algoQuePedir.domain.Exquisitos
import algoQuePedir.domain.Fieles
import algoQuePedir.domain.Impacientes
import algoQuePedir.domain.Inbox
import algoQuePedir.domain.IntercalaEdad
import algoQuePedir.domain.MailPublicitarioObserver
import algoQuePedir.domain.MailSender
import algoQuePedir.domain.Marketing
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.PedidoCertificadoObserver
import algoQuePedir.domain.Usuario
import algoQuePedir.domain.Veganos
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import local.Local
import algoQuePedir.domain.Plato
import java.time.LocalDate
import java.time.LocalDateTime


val userGus = Usuario(
    datos = datosGus,
    "19990906",
    direccion1,
)
val userAgus = Usuario(
    datos = datosAgus,
    "01012010",
    direccion2,
    condicion = Veganos
)
val usuario1 = Usuario(
    datos = datosUser1,
    fechaNacimiento = "10152010",
    ubicacion = direccionUser,
)
val usuario2 = Usuario(
    datos = datosUser2,
    fechaNacimiento = "01012010",
    ubicacion = direccion1,
)
val usuario3 = Usuario(
    datos = datosUser3,
    fechaNacimiento = "01012011",
    ubicacion = direccion1,
    fechaDeCreacion = LocalDate.of(2015, 10, 15)
)
val usuarioExquisito = Usuario(
    datos = datosGus,
    fechaNacimiento = "19990609",
    direccion1,
    condicion = Exquisitos
)
val usuarioConservador = Usuario(
    datos = datosGus,
    fechaNacimiento = "19990609",
    direccion1,
    condicion = Conservador
)
val usuarioFiel = Usuario(
    datos = datosGus,
    fechaNacimiento = "19990609",
    direccion1,
    condicion = Fieles,
)
val usuarioMarketing = Usuario(
    datos = datosGus,
    fechaNacimiento = "19990609",
    direccion1,
    condicion = Marketing
)
val usuarioImpaciente = Usuario(
    datos = datosGus,
    fechaNacimiento = "19990609",
    direc1,
    condicion = Impacientes
)

class UsuarioTest:DescribeSpec({
    userGus.agregarPreferido(salmon)
    userGus.agregarPreferido(ravioles)
    userGus.agregarEvitar(cafe)
    usuarioExquisito.agregarPreferido(salmon)
    usuarioExquisito.agregarPreferido(ravioles)
    usuarioExquisito.agregarEvitar(cafe)
    usuarioConservador.agregarPreferido(salmon)
    usuarioConservador.agregarPreferido(ravioles)
    usuarioConservador.agregarEvitar(cafe)
    usuarioFiel.agregarPreferido(salmon)
    usuarioFiel.agregarPreferido(ravioles)
    usuarioFiel.agregarEvitar(cafe)
    usuarioFiel.agregarLocalPreferido(pancheria)
    usuarioMarketing.agregarPreferido(salmon)
    usuarioMarketing.agregarPreferido(ravioles)
    usuarioMarketing.agregarEvitar(cafe)
    usuarioImpaciente.agregarPreferido(salmon)
    usuarioImpaciente.agregarPreferido(ravioles)
    usuarioImpaciente.agregarEvitar(cafe)

    beforeEach{
        userAgus.limpiarIngredientesEvitar()
        userAgus.limpiarIngredientesPreferidos()
        userAgus.agregarPreferido(salmon)
        userAgus.agregarEvitar(ravioles)
    }

    describe("Test info general user") {
        it("Test nombre") { userGus.datos.nombre shouldBe "Gustavo" }
        it("Test apellido") { userGus.datos.apellido shouldBe "Muinos" }
        it("Test username") { userGus.datos.username shouldBe "Gazzty" }
        it("Test password") { userGus.datos.password shouldBe "abc123" }
        it("Test fecha nacimiento") { userGus.fechaNacimiento shouldBe "19990906" }
        it("Test edad") { userGus.edad() shouldBe 26 }
        it("Test edad erronea") { userGus.edad() shouldNotBe 30 }
        it("Test Ingredientes preferidos") {userGus.ingredientesPreferidos shouldBe mutableListOf(salmon, ravioles)}
        it("Test Ingredientes a evitar") {userGus.ingredientesAEvitar shouldBe  mutableListOf(cafe)}
    }



    describe("Test intento de carga correcta de favorito") {
        it("Test Ingredientes preferidos originales") {userAgus.ingredientesPreferidos shouldBe mutableListOf(salmon)}
        it("Test Ingredientes a evitar") {userAgus.ingredientesAEvitar shouldBe  mutableListOf(ravioles)}
        it("Test Ingredientes preferidos post-ravioles") {
            userAgus.agregarPreferido(cafe)
            userAgus.ingredientesPreferidos shouldBe mutableListOf(salmon, cafe)
        }
    }

    describe("Test intento de carga erronea de favorito") {
        it("Test Ingredientes preferidos originales 2") {userAgus.ingredientesPreferidos shouldBe mutableListOf(salmon)}
        it("Test Ingredientes a evitar 2") {userAgus.ingredientesAEvitar shouldBe  mutableListOf(ravioles)}
        it("Test Ingredientes preferidos post-ravioles 2") {
            shouldThrow<RuntimeException> { userAgus.agregarPreferido(ravioles) }
        }
    }

    describe("Test criterios de plato"){
        it("Vegano"){
            userAgus.condicion = Veganos
            userAgus.aceptaPlato(pancho) shouldBe false
        }
        it("Exquisito"){
            userAgus.condicion = Exquisitos
            userAgus.aceptaPlato(pancho) shouldBe true
        }
        it("Conservador (sin fav)"){
            userAgus.ingredientesPreferidos = mutableListOf()
            userAgus.condicion = Conservador
            userAgus.aceptaPlato(pancho) shouldBe false
        }
        it("Conservador (con fav)"){
            userAgus.ingredientesPreferidos = mutableListOf(pan, salchicha)
            userAgus.condicion = Conservador
            userAgus.aceptaPlato(pancho) shouldBe true
        }
    }

    describe("Testear interacción usuario local"){
        it("Local cercano"){
            userGus.localCercano(pizzasYa) shouldBe true
        }
        it("Local no cercano"){
            userAgus.localCercano(pizzasYa) shouldBe false
        }
    }
    describe("Test hacer un pedido"){
        userGus.confirmarPedido(pedido1)
        userGus.confirmarPedido(pedido2)
        it("Validar que usuario agrega su pedido a la lista"){
            userGus.listaPedidos.contains(pedido1) shouldBe true
        }
        it("Validar fecha del ultimo pedido"){
            userGus.fechaUltimoPedido(pizzasYa) shouldBe pedido1.momentoDeOrden
        }
        it("Validar que se puede puntuar el local"){
            //condiciendo de TP3
            pizzasYa.pendiente=false
            //continua el test
            userGus.sePuedePuntuar(pizzasYa) shouldBe true
        }
        it("Validar que el puntaje llega al local"){
            pizzasYa.puntajes = mutableListOf()
            userGus.puntuarLocal(pizzasYa, 5)
            pizzasYa.puntajes[0] shouldBe 5
        }
    }
    describe("Test condiciones usuario"){
        it("Usuario sencillo deberia aceptar el plato"){
            userGus.aceptaPlato(pizza) shouldBe true
        }
        it("Usuario sencillo con plato con ingredientes a evitar"){
            userGus.aceptaPlato(cafeCortado) shouldBe false
        }
        it("Vegano NO deberia aceptar plato con carne"){
            userAgus.aceptaPlato(pancho) shouldBe false
        }
        it("Exquisito NO debería aceptar plato que no es de autor"){
            usuarioExquisito.aceptaPlato(pizza) shouldBe false
        }
        it("Conservador NO debería aceptar plato que no tenga ingrediente preferido"){
            usuarioConservador.aceptaPlato(cafeCortado) shouldBe false
        }
        it("Fiel NO debería aceptar plato que no sea de local preferido"){
            usuarioFiel.aceptaPlato(pizza) shouldBe false
        }
        it("Marketing NO debería aceptar plato que no contenga las palabras clave"){
            usuarioMarketing.aceptaPlato(pancho) shouldBe false
        }
        it("Impaciente NO debería aceptar plato de local lejano"){
            usuarioImpaciente.aceptaPlato(cafeCortado) shouldBe false
        }
    }

    describe("Condicion intercala edad"){
        val usuarioCondionEdad = Usuario(
            datos = datosGus,
            fechaNacimiento = "19990906",
            ubicacion = direccion1,
            condicion = IntercalaEdad
        ).apply {
            this.stubEdad = 25
            this.agregarPreferido(pan)
        }

        it("Edad impar, condicion Conservador"){
            /**val platoConservador = Plato(
                nombre = "Plato conservador",
                descripcion = "test",
                ingredientes = mutableListOf(pan),
                local = pizzasYa,
                descuento = 0.1,
                fechaLanzamiento = LocalDateTime.now().minusDays(5),
                valorBase = 50.0,
            ) **/
            usuarioCondionEdad.aceptaPlato(pizza) shouldBe true
        }

        usuarioCondionEdad.stubEdad = 24
        it("Edad par, condicion Exquisito"){
            usuarioCondionEdad.aceptaPlato(pizza) shouldBe false
        }
    }

    describe("Condiciones multiples"){
        val localVegano = Local(
            nombreLocal = "Pizzas Ya",
            porcentajeBeneficio = 0.25,
            direccion = direccionPizzeria1
        )
        val platoVegano = Plato(
            nombre = "Plato Vegano",
            descripcion = "test",
            ingredientes = mutableListOf(pan),
            local = localVegano,
            fechaLanzamiento = LocalDateTime.now().minusDays(5),
            valorBase = 50.0)
        val usuarioCondicionMultiple = Usuario(
            datos = datosGus,
            "19990906",
            direccion1,
            condicion = CondicionY(mutableListOf(Veganos, Fieles))
        ).apply { this.agregarLocalPreferido(localVegano) }

        it("Vegano y Fiel"){
            usuarioCondicionMultiple.aceptaPlato(pancho) && usuarioCondicionMultiple.aceptaPlato(platoVegano) shouldBe false
        }
    }

    describe("Cambiar condicion"){
        usuarioExquisito.cambiarCondicion(Conservador)
        it("Condicion debería ser conservador"){
            usuarioExquisito.condicion shouldBe Conservador
        }
    }

    /* OBSERVER TESTS */
    describe("Test observers"){
        val usuarioObservers = Usuario(datosGus, "19990906", direccion1)
        val localObserver = Local("localObserver", direccion1, porcentajeBeneficio = 5.0, menu = mutableListOf(cafeCortado))
        val mailPublicitarioSender = mockk<MailSender>(relaxed = true)
        val mailPublicitarioObserver = MailPublicitarioObserver(mailPublicitarioSender)
        val veganoObserver = CambiarAVeganoObserver()
        val pedidoObservers = Pedido(
            usuarioObservers, localObserver,
            platos = mutableListOf(platoRavioles),
            medioDePago = MedioDePago.EFECTIVO,
        )
        val pedidoMultipleObserver = Pedido(
            usuarioObservers, localObserver,
            platos = mutableListOf(platoRavioles),
            medioDePago = MedioDePago.EFECTIVO,
        )

        beforeEach {
            clearMocks(mailPublicitarioSender)
            usuarioObservers.clearObservers()
            usuarioObservers.clearCondicion()
        }

        it("Test observer mail"){
            usuarioObservers.subscribeObserver(mailPublicitarioObserver)
            usuarioObservers.confirmarPedido(pedidoObservers)
            verify (exactly = 1) {mailPublicitarioSender.sendMail(any())}
        }
        it("Test observer vegano"){
            usuarioObservers.subscribeObserver(veganoObserver)
            usuarioObservers.confirmarPedido(pedidoObservers)
            usuarioObservers.tieneCondicion(Veganos) shouldBe true
        }
        it("Test vegano y mail"){
            usuarioObservers.subscribeObserver(mailPublicitarioObserver)
            usuarioObservers.subscribeObserver(veganoObserver)
            usuarioObservers.confirmarPedido(pedidoMultipleObserver)

            verify (exactly = 1) {mailPublicitarioSender.sendMail(any())}
            usuarioObservers.tieneCondicion(Veganos) shouldBe true
        }
    }

    describe("Test observer de pedido certificado"){
        it("Test entra mensaje por pedido certificado") {
            val usuarioObservers = Usuario(datosGus, "19990906", direccion1)
            val observerPedidoCerti = PedidoCertificadoObserver()
            usuarioObservers.subscribeObserver(observerPedidoCerti)

            val unPedidoPrioritario = mockk<Pedido>(relaxed = true)
            every { unPedidoPrioritario.esCertificado() } returns true

            val unInbox = Inbox()
            val localObserver = Local("localObserver", direccion1, porcentajeBeneficio = 5.0, menu = mutableListOf(cafeCortado))
            localObserver.inbox = unInbox
            every { unPedidoPrioritario.local } returns localObserver

            usuarioObservers.confirmarPedido(unPedidoPrioritario)
            localObserver.inbox.mensajes[0].contenido shouldBe "Priorizar el pedido"
        }
    }

})
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.round
import local.Local
import cupon.*
import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Pedido
import io.mockk.every
import io.mockk.mockk
import algoQuePedir.domain.Plato

//CUPONES SIMPLES
val unCuponSimple = cuponDescuentoSegunTope(
    fechaEmision = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)),
    duracion = 15,
    porcentajeBase = 15.0,
    tope = 50.0,
    porcentajeAdicional= 15.0
)

val unCuponSimple2 = cuponDescuentoSegunTope(
    //LocalDateTime.of(2025, 5, 1, 12, 0)
    //LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30))
    fechaEmision = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)),
    duracion = 15,
    porcentajeBase = 15.0,
    tope = 50.0,
    porcentajeAdicional= 15.0
)

val unCuponSimpleVencido = cuponDescuentoSegunTope(
    fechaEmision = LocalDateTime.now().minusDays(30),
    duracion = 15,
    porcentajeBase = 15.0,
    tope = 50.0,
    porcentajeAdicional= 15.0
)
//CUPONES SIMPLES

val unCuponSegunTopeAlto= cuponDescuentoSegunTope(
    fechaEmision = LocalDateTime.of(2025, 4, 19, 12, 0),
    duracion = 15,
    porcentajeBase = 115.0,
    tope = 100.0,
    porcentajeAdicional= 15.0
)
val unCuponSegunTope= cuponDescuentoSegunTope(
    fechaEmision = LocalDateTime.now().minusDays(5),
    duracion = 15,
    porcentajeBase = 15.0,
    tope = 50.0,
    porcentajeAdicional= 15.0
)

//PEDIDOS
//PIZZA sale 135
//PANCHO sale 225
val elPedido1 = Pedido(
    cliente = usuario1,
    local = pizzasYa,
    platos = mutableListOf(pizza, pizza, pancho),
    medioDePago = MedioDePago.EFECTIVO,
    estadoDelPedido = EstadoDelPedido.PREPARADO,
    momentoDeOrden = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)),
)

val elPedido4 = Pedido(
    cliente = usuario1,
    local = laToscana,
    platos = mutableListOf(pancho),
    medioDePago = MedioDePago.EFECTIVO,
    estadoDelPedido = EstadoDelPedido.PREPARADO,
    momentoDeOrden = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)),
)

class CuponTest:DescribeSpec ({

    describe("Pruebo vencimiento de los cupones"){
        it("El cupón NO esta vencido"){
            unCuponSimple.cuponActivo(elPedido1) shouldBe true
        }
        it("El cupón ESTA vencido"){
            unCuponSimpleVencido.cuponActivo(elPedido1) shouldBe false
        }
    }

    unCuponSimple.descuentoTotal(elPedido1)
    describe("Aplico cupón y compruebo si se pueda aplicar"){
        it("Evaluo cupón que ya fue aplicado"){
            unCuponSimple.puedoAplicar(elPedido1) shouldBe false
        }
        it("Evaluo cupón que NO fue aplicado"){
            unCuponSimple2.puedoAplicar(elPedido1) shouldBe true
        }
    }

    describe("Compruebo si se pueda aplicar en base a si el descuento supera al pedido"){
        it("Monto supera al pedido"){
            unCuponSegunTopeAlto.puedoAplicar(elPedido4) shouldBe false
        }
        it("Monto NO supera al pedido"){
            unCuponSegunTope.puedoAplicar(elPedido4) shouldBe true
        }
    }

    describe("Compruebo si el cupón según LOCAL puede aplicarse"){

        //LOCALES
        val pizzasYa = Local(
            nombreLocal = "Pizzas Ya",
            porcentajeBeneficio = 0.25,
            direccion = direccionPizzeria1
        )
        val pancheria = Local(
            nombreLocal = "Pancheria",
            porcentajeBeneficio = 0.15,
            direccion = direccion1
        )
        //LOCALES

        //CUPONES
        val unCuponSegunLocal1= cuponDescuentoSegunLocal(
            fechaEmision = LocalDateTime.of(2025, 5, 1, 12, 0),
            duracion = 30,
            porcentajeBase = 15.0,
            localesDescuento = mutableListOf(pizzasYa)
        )

        val unCuponSegunLocal2= cuponDescuentoSegunLocal(
            fechaEmision = LocalDateTime.of(2025, 5, 1, 12, 0),
            duracion = 30,
            porcentajeBase = 15.0,
            localesDescuento = mutableListOf(pizzasYa)
        )

        val unCuponSegunLocal3= cuponDescuentoSegunLocal(
            fechaEmision = LocalDateTime.of(2025, 5, 1, 12, 0),
            duracion = 30,
            porcentajeBase = 15.0,
            localesDescuento = mutableListOf(pancheria)
        )
        //CUPONES

        val pedidoCertificadoYLocal = mockk<Pedido>()
        every { pedidoCertificadoYLocal.sumaPlatos() } returns 2000.0
        every { pedidoCertificadoYLocal.esCertificado() } returns true
        every { pedidoCertificadoYLocal.local } returns pizzasYa
        every { pedidoCertificadoYLocal.momentoDeOrden } returns LocalDateTime.of(2025, 5, 1, 12, 0)

        val pedidoLocal = mockk<Pedido>()
        every { pedidoLocal.sumaPlatos() } returns 2000.0
        every { pedidoLocal.esCertificado() } returns false
        every { pedidoLocal.local } returns pizzasYa
        every { pedidoLocal.momentoDeOrden } returns LocalDateTime.of(2025, 5, 1, 12, 0)

        val pedidoSolo = mockk<Pedido>()
        every { pedidoSolo.sumaPlatos() } returns 2000.0
        every { pedidoSolo.esCertificado() } returns false
        every { pedidoSolo.local } returns pizzasYa
        every { pedidoSolo.momentoDeOrden } returns LocalDateTime.of(2025, 5, 1, 12, 0)

        it("Aplica el descuento especial por LOCAL y el descuento base siendo certificado"){
            round(unCuponSegunLocal1.descuentoTotal(pedidoCertificadoYLocal)) shouldBe round(1300.0)
        }

        it("Aplica el descuento especial por LOCAL y el descuento base NO siendo certificado"){
            round(unCuponSegunLocal2.descuentoTotal(pedidoLocal)) shouldBe round(800.0)
        }

        it("No aplica el descuento ya que el LOCAL no esta incluido"){
            round(unCuponSegunLocal3.descuentoTotal(pedidoSolo)) shouldBe round(0.0)
        }
    }

    describe("Compruebo si el cupón según DIA puede aplicarse"){
        //CUPONES
        val unCuponSegunDia= cuponDescuentoSegunDia(
            fechaEmision = LocalDateTime.of(2025, 5, 4, 12, 0),
            duracion = 10,
            porcentajeBase = 15.0,
            diaSemana = 7
        )

        val otroCuponSegunDia= cuponDescuentoSegunDia(
            fechaEmision = LocalDateTime.of(2025, 5, 4, 12, 0),
            duracion = 10,
            porcentajeBase = 15.0,
            diaSemana = 7
        )
        //CUPONES

        //PLATOS
        val platoMockAplica = mockk<Plato>()
        every { platoMockAplica.fechaLanzamiento } returns LocalDateTime.of(2025, 5, 4, 12, 0)

        val platoMockNoAplica = mockk<Plato>()
        every { platoMockNoAplica.fechaLanzamiento } returns LocalDateTime.of(2025, 5, 3, 12, 0)
        //PLATOS

        //PEDIDOS
        val pedidoDiaAplicaDescuentoMax = mockk<Pedido>()
        every { pedidoDiaAplicaDescuentoMax.momentoDeOrden } returns LocalDateTime.of(2025, 5, 4, 12, 0)
        every { pedidoDiaAplicaDescuentoMax.sumaPlatos() } returns 2000.0
        every { pedidoDiaAplicaDescuentoMax.platos } returns mutableListOf(platoMockAplica)

        val pedidoDiaAplicaDescuentoMin = mockk<Pedido>()
        every { pedidoDiaAplicaDescuentoMin.momentoDeOrden } returns LocalDateTime.of(2025, 5, 4, 12, 0)
        every { pedidoDiaAplicaDescuentoMin.sumaPlatos() } returns 2000.0
        every { pedidoDiaAplicaDescuentoMin.platos } returns mutableListOf(platoMockNoAplica)

        val pedidoDiaNoAplica = mockk<Pedido>()
        every { pedidoDiaNoAplica.momentoDeOrden } returns LocalDateTime.of(2025, 5, 3, 12, 0)
        every { pedidoDiaNoAplica.sumaPlatos() } returns 2000.0
        every { pedidoDiaNoAplica.platos } returns mutableListOf(platoMockAplica)
        //PEDIDOS

        it("Aplica el descuento especial por DIA por el 10%"){
            round(unCuponSegunDia.descuentoTotal(pedidoDiaAplicaDescuentoMax)) shouldBe round(500.0)
        }
        it("No aplica el descuento especial por DIA por el 5%"){
            round(otroCuponSegunDia.descuentoTotal(pedidoDiaAplicaDescuentoMin)) shouldBe round(400.0)
        }
        it("No aplica el cupon directamente"){
            round(unCuponSegunDia.descuentoTotal(pedidoDiaNoAplica)) shouldBe round(0.0)
        }
    }

    describe("Compruebo si el cupón según TOPE puede aplicarse"){

        val pedidoTope = mockk<Pedido>()
        every { pedidoTope.sumaPlatos() } returns 500.0
        //CUPONES
        val unCuponSegunTope= cuponDescuentoSegunTope(
            fechaEmision = LocalDateTime.of(2025, 4, 19, 12, 0),
            duracion = 15,
            porcentajeBase = 15.0,
            tope = 100.0,
            porcentajeAdicional= 15.0
        )
        val unCuponSegunTopeBajo= cuponDescuentoSegunTope(
            fechaEmision = LocalDateTime.of(2025, 4, 19, 12, 0),
            duracion = 15,
            porcentajeBase = 15.0,
            tope = 50.0,
            porcentajeAdicional= 15.0
        )
        //CUPONES
        it("El descuento especial no alcanza el TOPE"){
            round(unCuponSegunTope.descuentoEspecial(pedidoTope)) shouldBe round(75.0)
        }
        it("El descuento especial alcanza el TOPE"){
            round(unCuponSegunTopeBajo.descuentoEspecial(pedidoTope)) shouldBe round(50.0)
        }
    }

})
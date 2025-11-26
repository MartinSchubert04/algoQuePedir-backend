import cupon.Cupon
import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Pedido
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.round

import io.mockk.every
import io.mockk.mockk

val pedido1 = Pedido(
    cliente = usuario1,
    local = pizzasYa,
    platos = mutableListOf(pizza, pizza, pizza, pizza),
    medioDePago = MedioDePago.EFECTIVO,
    estadoDelPedido = EstadoDelPedido.PREPARADO,
    momentoDeOrden = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)),
)
val pedido2 = Pedido(
    cliente = usuario2,
    local = pancheria,
    platos = mutableListOf(),
    medioDePago = MedioDePago.EFECTIVO
)
val pedido3 = Pedido(
    cliente = usuario3,
    local = pancheria,
    platos = mutableListOf(),
    medioDePago = MedioDePago.EFECTIVO,
    cupon = unCuponSimple
)
val pedido4 = Pedido(
    cliente = usuario3,
    local = comidaLejos,
    platos = mutableListOf(),
    estadoDelPedido = EstadoDelPedido.PREPARADO,
    medioDePago = MedioDePago.EFECTIVO,
)

class PedidoTest:DescribeSpec ({
    usuario1.agregarEvitar(pan)
    
    describe("Permiso de agregar un plato"){
        it("No permite por preferencias del usuario"){
            usuario2.agregarEvitar(pan)
            shouldThrow<RuntimeException>{pedido2.agregarPlato(pancho)}
            pedido2.platos shouldBe mutableListOf()
        }
        it("No permite por local incorrecto"){
            shouldThrow<RuntimeException>{pedido2.agregarPlato(pizza)}
            pedido2.platos shouldBe mutableListOf()
        }
        it("Pemrite agregar"){
            pedido3.agregarPlato(pancho)
            pedido3.platos shouldBe mutableListOf(pancho)
        }
    }

    describe("Calculo de totales a pagar"){
        beforeEach {
            pedido3.platos = mutableListOf()
            pedido3.cupon = null
        }
        it("Pago en efectivo sin comision"){
            pedido3.agregarPlato(pancho)
            pedido3.totalAPagar() shouldBe 262.65
        }
        it("Pago con QR con 5% de comision"){
            pedido3.agregarPlato(pancho)
            pedido3.medioDePago = MedioDePago.QR
            round(pedido3.totalAPagar()) shouldBe round(275.7825)
        }
    }

    describe("Verificar si es certificado"){
        it("Es certificado"){
            pancheria.aceptarPuntaje(5)
            pedido3.esCertificado() shouldBe true
        }
        it ("No es certificado"){
            pancheria.aceptarPuntaje(2)
            pedido3.esCertificado() shouldBe false
        }
    }

    describe("Aceptar un delivery"){
        it("permite aceptar el delivery"){
            pedido1.aceptarDelivery(delivery3)
            pedido1.delivery shouldBe delivery3
        }
        it("no permite aceptar el delivery"){
            shouldThrow<RuntimeException>{pedido4.aceptarDelivery(delivery3)}
            pedido4.delivery shouldBe null
        }
    }


    val cuponMockk = mockk<Cupon>()
    every { cuponMockk.descuentoTotal(pedido3) } returns 50.0

    describe("Cupones"){
        beforeEach {
            pedido3.platos.clear()
            pedido3.cupon = cuponMockk
        }

        it("precio final con cupones"){
            pedido3.agregarPlato(pancho)
            pedido3.medioDePago = MedioDePago.QR

            pedido3.totalAPagar() shouldBe 225.78249999999997
        }

        it("sin cupon"){
            pedido3.agregarPlato(pancho)
            pedido3.cupon = null
            pedido3.medioDePago = MedioDePago.QR
            round(pedido3.totalAPagar()) shouldBe round(275.7825)
        }
    }

})

import algoQuePedir.domain.Delivery
import algoQuePedir.domain.CriterioCertificado
import algoQuePedir.domain.CriterioHorarioSeguro
import algoQuePedir.domain.CriterioMontoMinimo
import algoQuePedir.domain.CriterioO
import algoQuePedir.domain.CriterioSoloLocalesAmigos
import algoQuePedir.domain.CriterioY
import algoQuePedir.domain.SencilloDel
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon

val delivery1 = Delivery(
    datos = datosDel1,
    //se cambia el razonamiento de la zona de trabajo
    // la zona de trabajo la define el delivery no el local
    // estilo rappi,pedidoya
    zonaTrabajo = Polygon().apply {
        add(Point(0.0, 0.0))
        add(Point(20.0, 0.0))
        add(Point(20.0, 20.0))
        } ,
    )
val delivery2 = Delivery(
    datos = datosDel2,
    zonaTrabajo = Polygon().apply {
        add(Point(0.0, 0.0))
        add(Point(1.0, 0.0))
        add(Point(1.0, 1.0))
        add(Point(0.0, 0.0))}
)

val delivery3 = Delivery(
    datos = datosDel3,
    zonaTrabajo = Polygon().apply {
        add(Point(0.0, 0.0))
        add(Point(20.0, 0.0))
        add(Point(20.0, 20.0))
        } ,
)

// DEJO ESTAS CLASES LOCAL Y PEDIDO PARA HACER LOS TEST DESPUES SE TIENE QUE CAMBIAR POR LAS DE VERDAD
class DeliveryTest : DescribeSpec({

    describe("Tests de aceptación de pedidos") {
        it("Debería aceptar un pedido listo dentro de la zona y cumpliendo todas las condiciones") {
            delivery1.puedeAceptarPedido(pedido1) shouldBe true
        }

        it("No debería aceptar un pedido fuera del horario seguro") {
            delivery1.criterioAceptacion = CriterioHorarioSeguro(13..21)
            delivery1.puedeAceptarPedido(pedido1) shouldBe false
        }

        it("Debería aceptar un pedido con monto insuficiente") {
            delivery1.criterioAceptacion = CriterioMontoMinimo(200.0)
            delivery1.puedeAceptarPedido(pedido1) shouldBe true
        }

        it("No debería aceptar un pedido de un local no amigo") {
            delivery1.criterioAceptacion = CriterioSoloLocalesAmigos(mutableListOf())
            delivery1.puedeAceptarPedido(pedido1) shouldBe false
        }

        it("Debería aceptar un pedido de un local amigo") {
            delivery1.criterioAceptacion = CriterioSoloLocalesAmigos(mutableListOf(pizzasYa))
            delivery1.puedeAceptarPedido(pedido1) shouldBe true
        }

        it("No debería aceptar un pedido no certificado") {
            pizzasYa.aceptarPuntaje(2)
            delivery1.criterioAceptacion = CriterioCertificado()
            delivery1.puedeAceptarPedido(pedido1) shouldBe false
        }

        it("No debería aceptar un pedido FUERA de la zona de trabajo") {
            delivery2.criterioAceptacion = SencilloDel
            delivery2.puedeAceptarPedido(pedido1) shouldBe false
        }
    }


    it("Criterios multiples debe aceptar alguno de los 2 criterios"){
        delivery1.criterioAceptacion = CriterioO(
            listOf(
                CriterioCertificado(), // este criterio es falso
                CriterioMontoMinimo(200.0)
            )
        )
        delivery1.puedeAceptarPedido(pedido1) shouldBe true
    }

    it("criterios multiple debe aceptar los 2 criterios"){

        delivery1.criterioAceptacion = CriterioY(
            listOf(
                CriterioHorarioSeguro(10..21),
                CriterioSoloLocalesAmigos(mutableListOf(pizzasYa))
            )
        )
        delivery1.puedeAceptarPedido(pedido1) shouldBe true
    }

    it("criterios multiple debe aceptar los 2 criterios pero uno es falso"){

        delivery1.criterioAceptacion = CriterioY(
            listOf(
                CriterioCertificado(),
                CriterioSoloLocalesAmigos(mutableListOf(pizzasYa))
            )
        )
        delivery1.puedeAceptarPedido(pedido1) shouldBe false
    }
})


import algoQuePedir.domain.AccionEstablecerPedido
import algoQuePedir.domain.AccionPuntuarAleatorio
import algoQuePedir.domain.AccionPuntuarConPromedio
import algoQuePedir.domain.AccionPuntuarFijo
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeBetween

class AccionUsuarioTest : DescribeSpec({


    describe("AccionEstablecerPedido") {
        it("agrega el pedido al usuario") {
            val accion = AccionEstablecerPedido(pedido1)
            usuarioFiel.listaPedidos.size shouldBe 0
            accion.ejecutar(usuarioFiel)
            usuarioFiel.listaPedidos.size shouldBe 1
        }
    }

    describe("AccionPuntuarAleatorio") {
        it("puntúa con valor entre 1 y 10") {
            val accion1 = AccionEstablecerPedido(
                pedido1
            )
            accion1.ejecutar(usuarioFiel)

            val accion2 = AccionPuntuarAleatorio(pizzasYa)
            accion2.ejecutar(usuarioFiel)

            val ultimoPuntaje = pizzasYa.puntajes.last()
            ultimoPuntaje.shouldBeBetween(1,10)
        }
    }



    describe("AccionPuntuarConPromedio") {
        it("puntúa con el promedio redondeado") {
            val accion1 = AccionEstablecerPedido(
                pedido1
            )
            pizzasYa.pendiente=false
            pizzasYa.puntajes.addAll(listOf(10, 5))
            accion1.ejecutar(usuarioFiel)
            val promedioEsperado = pizzasYa.puntajes.average().toInt()
            val accion2 = AccionPuntuarConPromedio(pizzasYa)

            accion2.ejecutar(usuarioFiel)

            pizzasYa.puntajes.last() shouldBe promedioEsperado
        }
    }

    describe("AccionPuntuarFijo") {
        it("Puntaje fijo siempre"){
            val accion1 = AccionEstablecerPedido(
                pedido4
            )

            accion1.ejecutar(usuarioFiel)
            comidaLejos.pendiente=false
            val accion2 = AccionPuntuarFijo(comidaLejos, 7)
            accion2.ejecutar(usuarioFiel)
            comidaLejos.puntajes.last() shouldBe 7
        }
    }

    describe("Compruebo que no se puede puntuar 2 veces devido a local ya no pendiente ") {
        it("Puntaje fijo siempre"){
            val accion1 = AccionEstablecerPedido(
                pedido1
            )

            accion1.ejecutar(usuarioFiel)

            val accion2 = AccionPuntuarFijo(pizzasYa, 7)
            //accion2.ejecutar(usuarioFiel)
         //   pizzasYa.puntajes.last() shouldBe 7
            //ejecuto de nuevo
            val exception = shouldThrow<RuntimeException> {
                accion2.ejecutar(usuarioFiel)
            }
            exception.message shouldBe "Este local ya fue puntuado"
        }
    }
})
/**
**/
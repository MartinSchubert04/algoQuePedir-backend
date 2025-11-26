import algoQuePedir.domain.Auditores
import algoQuePedir.domain.EjecutarAuditoria
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.auditorCombinado
import algoQuePedir.domain.auditorPLatosVeganos
import algoQuePedir.domain.auditorPlatosPorPedido
import algoQuePedir.domain.auditorVentasAcumuladas
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import algoQuePedir.domain.Plato
import algoQuePedir.domain.Ingrediente
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import local.Local

class AuditoresTest : DescribeSpec({

    describe("Probamos command de auditorias") {

        it("PROBAMOS AUDITOR QUE CALCULA EL MONTO ALCANZADO") {
            val localMock = mockk<Local>()

            // Mockeamos 2 pedidos
            val pedidoMock1 = mockk<Pedido>()
            every { pedidoMock1.totalAPagar() } returns 3000.00
            val pedidoMock2 = mockk<Pedido>()
            every { pedidoMock2.totalAPagar() } returns 4000.00

            val pedidosListaMock = mutableListOf(pedidoMock1, pedidoMock2)

            every { localMock.listaPedidos } returns pedidosListaMock
            every { localMock.nombreLocal } returns "El Fortin"

            val auditarMontoVentas = auditorVentasAcumuladas(5000.00)
            val commandMontoAlcanzado = EjecutarAuditoria(auditarMontoVentas)

            val exception = shouldThrow<RuntimeException> {
                commandMontoAlcanzado.ejecutar(localMock)
            }
            exception.message shouldBe "Ventas mayores al monto seteado"

        }

        it("PROBAMOS AUDITOR QUE MIDE QUE AL MENOS HAYAN 5 O MAS PEDIDOS CON 3 O MAS PLATOS CADA UNO") {        //
            val localMock = mockk<Local>()

            val platoMock1 = mockk<Plato>()
            val platoMock2 = mockk<Plato>()
            val platoMock3 = mockk<Plato>()

            val platoMock4 = mockk<Plato>()
            val platoMock5 = mockk<Plato>()
            val platoMock6 = mockk<Plato>()
            val platoMock7 = mockk<Plato>()

            val platoMock8 = mockk<Plato>()
            val platoMock9 = mockk<Plato>()
            val platoMock10 = mockk<Plato>()
            val platoMock11 = mockk<Plato>()

            val platoMock12 = mockk<Plato>()
            val platoMock13 = mockk<Plato>()
            val platoMock14 = mockk<Plato>()

            val platoMock15 = mockk<Plato>()
            val platoMock16 = mockk<Plato>()
            val platoMock17 = mockk<Plato>()

            // Mockeamos 5 pedidos
            val pedidoMock1 = mockk<Pedido>()
            every { pedidoMock1.platos } returns mutableListOf(platoMock1, platoMock2, platoMock3)
            val pedidoMock2 = mockk<Pedido>()
            every { pedidoMock2.platos } returns mutableListOf(platoMock4, platoMock5, platoMock6, platoMock7)
            val pedidoMock3 = mockk<Pedido>()
            every { pedidoMock3.platos } returns mutableListOf(platoMock8, platoMock9, platoMock10, platoMock11)
            val pedidoMock4 = mockk<Pedido>()
            every { pedidoMock4.platos } returns mutableListOf(platoMock12, platoMock13, platoMock14)
            val pedidoMock5 = mockk<Pedido>()
            every { pedidoMock5.platos } returns mutableListOf(platoMock15, platoMock16, platoMock17)

            val pedidosListaMock = mutableListOf(pedidoMock1, pedidoMock2, pedidoMock3, pedidoMock4, pedidoMock5)

            every { localMock.listaPedidos } returns pedidosListaMock
            every { localMock.nombreLocal } returns "El Fortin"

            val auditarCantiPlatos = auditorPlatosPorPedido()
            val commandCantidadPlatos = EjecutarAuditoria(auditarCantiPlatos)

            val exception = shouldThrow<RuntimeException> {
                commandCantidadPlatos.ejecutar(localMock)
            }
            exception.message shouldBe "Se vendieron mas de 5 veces 3 o más platos"

        }

        it("PROBAMOS AUDITOR QUE CALCULA LA CANTIDAD DE PEDIDOS VEGANOS") {
            val localMock = mockk<Local>()

            val ingredienteVegano1 = mockk<Ingrediente>()
            every { ingredienteVegano1.esDeOrigenAnimal } returns false
            val ingredienteVegano2 = mockk<Ingrediente>()
            every { ingredienteVegano2.esDeOrigenAnimal } returns false
            val ingredienteVegano3 = mockk<Ingrediente>()
            every { ingredienteVegano3.esDeOrigenAnimal } returns false

            val platoVegano1 = mockk<Plato>()
            every { platoVegano1.ingredientes } returns mutableListOf(ingredienteVegano1)
            val platoVegano2 = mockk<Plato>()
            every { platoVegano2.ingredientes } returns mutableListOf(ingredienteVegano2)
            val platoVegano3 = mockk<Plato>()
            every { platoVegano3.ingredientes } returns mutableListOf(ingredienteVegano3)

            // Mockeamos 3 pedidos
            val pedidoMock1 = mockk<Pedido>()
            every { pedidoMock1.platos } returns mutableListOf(platoVegano1)
            val pedidoMock2 = mockk<Pedido>()
            every { pedidoMock2.platos } returns mutableListOf(platoVegano2)
            val pedidoMock3 = mockk<Pedido>()
            every { pedidoMock3.platos } returns mutableListOf(platoVegano3)
            val pedidosListaMock = mutableListOf(pedidoMock1,pedidoMock2,pedidoMock3)

            every { localMock.listaPedidos } returns pedidosListaMock
            every { localMock.nombreLocal } returns "El Fortin"

            val auditarVentas = auditorPLatosVeganos(3)
            val commandCantidadVeganos = EjecutarAuditoria(auditarVentas)

            val exception = shouldThrow<RuntimeException> {
                commandCantidadVeganos.ejecutar(localMock)
            }
            exception.message shouldBe "Se alcanzo la meta de platos veganos"

        }

        it("PROBAMOS AUDITOR QUE MIDE TODOS LOS OBJETIVOS A LA VEZ") {

            val localMock = mockk<Local>()

            // Mockeamos 2 platos para los pedidos que usamos para calcular determinado monto
            val platoMockMonto1 = mockk<Plato>()
            every { platoMockMonto1.ingredientes } returns mutableListOf()
            val platoMockMonto2 = mockk<Plato>()
            every { platoMockMonto2.ingredientes } returns mutableListOf()

            // Mockeamos 2 pedidos con determinado monto
            val pedidoMock1 = mockk<Pedido>()
            every { pedidoMock1.totalAPagar() } returns 3000.00
            every { pedidoMock1.platos } returns mutableListOf(platoMockMonto1)
            val pedidoMock2 = mockk<Pedido>()
            every { pedidoMock2.totalAPagar() } returns 4000.00
            every { pedidoMock2.platos } returns mutableListOf(platoMockMonto2)

            // Mockeamos platos

            val platoMock1 = mockk<Plato>()
            every { platoMock1.ingredientes } returns mutableListOf()
            val platoMock2 = mockk<Plato>()
            every { platoMock2.ingredientes } returns mutableListOf()
            val platoMock3 = mockk<Plato>()
            every { platoMock3.ingredientes } returns mutableListOf()

            val platoMock4 = mockk<Plato>()
            every { platoMock4.ingredientes } returns mutableListOf()
            val platoMock5 = mockk<Plato>()
            every { platoMock5.ingredientes } returns mutableListOf()
            val platoMock6 = mockk<Plato>()
            every { platoMock6.ingredientes } returns mutableListOf()
            val platoMock7 = mockk<Plato>()
            every { platoMock7.ingredientes } returns mutableListOf()

            val platoMock8 = mockk<Plato>()
            every { platoMock8.ingredientes } returns mutableListOf()
            val platoMock9 = mockk<Plato>()
            every { platoMock9.ingredientes } returns mutableListOf()
            val platoMock10 = mockk<Plato>()
            every { platoMock10.ingredientes } returns mutableListOf()
            val platoMock11 = mockk<Plato>()
            every { platoMock11.ingredientes } returns mutableListOf()

            val platoMock12 = mockk<Plato>()
            every { platoMock12.ingredientes } returns mutableListOf()
            val platoMock13 = mockk<Plato>()
            every { platoMock13.ingredientes } returns mutableListOf()
            val platoMock14 = mockk<Plato>()
            every { platoMock14.ingredientes } returns mutableListOf()

            val platoMock15 = mockk<Plato>()
            every { platoMock15.ingredientes } returns mutableListOf()
            val platoMock16 = mockk<Plato>()
            every { platoMock16.ingredientes } returns mutableListOf()
            val platoMock17 = mockk<Plato>()
            every { platoMock17.ingredientes } returns mutableListOf()

            // Mockeamos 5 pedidos para calcular cantidad de platos por pedido
            val pedidoMock3 = mockk<Pedido>()
            every { pedidoMock3.platos } returns mutableListOf(platoMock1, platoMock2, platoMock3)
            every { pedidoMock3.totalAPagar() } returns 4000.00
            val pedidoMock4 = mockk<Pedido>()
            every { pedidoMock4.platos } returns mutableListOf(platoMock4, platoMock5, platoMock6, platoMock7)
            every { pedidoMock4.totalAPagar() } returns 4000.00
            val pedidoMock5 = mockk<Pedido>()
            every { pedidoMock5.platos } returns mutableListOf(platoMock8, platoMock9, platoMock10, platoMock11)
            every { pedidoMock5.totalAPagar() } returns 4000.00
            val pedidoMock6 = mockk<Pedido>()
            every { pedidoMock6.platos } returns mutableListOf(platoMock12, platoMock13, platoMock14)
            every { pedidoMock6.totalAPagar() } returns 4000.00
            val pedidoMock7 = mockk<Pedido>()
            every { pedidoMock7.platos } returns mutableListOf(platoMock15, platoMock16, platoMock17)
            every { pedidoMock7.totalAPagar() } returns 4000.00

            // Mockeamos ingredientes veganos para los platos
            val ingredienteVegano1 = mockk<Ingrediente>()
            every { ingredienteVegano1.esDeOrigenAnimal } returns false
            val ingredienteVegano2 = mockk<Ingrediente>()
            every { ingredienteVegano2.esDeOrigenAnimal } returns false

            val platoVegano1 = mockk<Plato>()
            every { platoVegano1.ingredientes } returns mutableListOf(ingredienteVegano1)
            val platoVegano2 = mockk<Plato>()
            every { platoVegano2.ingredientes } returns mutableListOf(ingredienteVegano2)

            // Mockeamos 2 pedidos mas para los platos veganos
            val pedidoMock8 = mockk<Pedido>()
            every { pedidoMock8.platos } returns mutableListOf(platoVegano1)
            every { pedidoMock8.totalAPagar() } returns 4000.00
            val pedidoMock9 = mockk<Pedido>()
            every { pedidoMock9.platos } returns mutableListOf(platoVegano2)
            every { pedidoMock9.totalAPagar() } returns 4000.00

            val pedidosListaMock = mutableListOf(
                pedidoMock1,
                pedidoMock2,
                pedidoMock3,
                pedidoMock4,
                pedidoMock5,
                pedidoMock6,
                pedidoMock7,
                pedidoMock8,
                pedidoMock9
            )

            every { localMock.listaPedidos } returns pedidosListaMock
            every { localMock.nombreLocal } returns "El Fortin"

            val auditor1 = auditorVentasAcumuladas(5000.0)
            val auditor2 = auditorPlatosPorPedido()
            val auditor3 = auditorPLatosVeganos(2)

            val listaDeAuditores = listOf<Auditores>(auditor1, auditor2, auditor3)
            val auditarVentas = auditorCombinado(listaDeAuditores)
            val commandAuditarTodo = EjecutarAuditoria(auditarVentas)

            commandAuditarTodo.ejecutar(localMock) shouldBe "Todas las auditorias fueron exitosas"

        }
    }
})
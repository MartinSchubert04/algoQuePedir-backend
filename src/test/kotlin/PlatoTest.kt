import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import algoQuePedir.domain.Plato
import java.time.LocalDateTime

val pizza = Plato(
    nombre = "Pizza",
    descripcion = "pizza de muzzarela con salsa de tomate",
    ingredientes = mutableListOf(pan),
    local = pizzasYa,
    descuento = 0.1,
    fechaLanzamiento = LocalDateTime.now().minusDays(5),
    valorBase = 50.0
)
val pancho = Plato(
    nombre = "Pancho",
    descripcion = "pan con salchicha",
    ingredientes = mutableListOf(pan,salchicha),
    local = pancheria,
    descuento = 0.1,
    fechaLanzamiento = LocalDateTime.now().minusDays(5), // es para que el plato siempre salga hace 5 dias,
    esDeAutor = true,
    porcentajeRegalia = 0.03,
    valorBase = 50.0
)
val cafeCortado = Plato(
    nombre = "Cafe cortado",
    descripcion = "Cafe cortado con leche",
    ingredientes = mutableListOf(cafe),
    local = pancheria,
    descuento = 0.1,
    fechaLanzamiento = LocalDateTime.now().minusDays(5), // es para que el plato siempre salga hace 5 dias,
    esDeAutor = true,
    porcentajeRegalia = 0.03,
    valorBase = 50.0
)
val platoRavioles = Plato(
    nombre = "Ravioles",
    descripcion = "",
    ingredientes = mutableListOf(ravioles),
    local = pancheria,
    descuento = 0.0,
    fechaLanzamiento = LocalDateTime.now().minusDays(20),
    esDeAutor = true,
    porcentajeRegalia = 0.5,
    valorBase = 10000.0
)

public class PlatoTest:DescribeSpec({

    describe("Test entrega 0") {

        it("Test nombre") { pancho.nombre shouldBe "Pancho" }
        it("Test descripcion") { pancho.descripcion shouldBe "pan con salchicha" }
        it("Test costo de prod.") { pancho.costoProduccion shouldBe 250 }
    }
    describe("Test entrega 1"){
        // pancho de autor con regalias y nuevo
        it("Tiene descuento") { pancho.esPromo() shouldBe true}
        it("es nuevo?") { pancho.esNuevo() shouldBe true }
        it("valor base mas procentaje del local y costo de prod") {
            pancho.valorBaseAplicado() shouldBe 307.5
        }
        it("precio sin descuento") { pancho.precioSinDescuentos() shouldBe 309}
        it("valor de regalia") { pancho.agregarRegalia() shouldBe 1.5}
        it("Precio final") { pancho.precioFinal() shouldBe 231.75 }
    }
})

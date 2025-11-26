import algoQuePedir.domain.MedioDePago
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import local.Local

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
val comidaLejos = Local(
    nombreLocal = "Hamburguesas Muy Lejos",
    porcentajeBeneficio = 0.15,
    direccion = direccion3
)
val elFortin = Local(
    nombreLocal = "El Fortin",
    direccion = direFortin,
    porcentajeBeneficio = 0.2,
    mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
)
val laToscana = Local(
    nombreLocal = "La Toscana",
    direccion = direToscana,
    porcentajeBeneficio = 0.30,
    mediosDePago = mutableListOf(MedioDePago.EFECTIVO, MedioDePago.TRANSFERENCIA, MedioDePago.QR),
)
val laPancheria = Local(
    nombreLocal = "Peters",
    direccion = direPeters,
    porcentajeBeneficio = 0.15,
    mediosDePago = mutableListOf(MedioDePago.EFECTIVO, MedioDePago.QR),
)

class LocalTest:DescribeSpec({

    describe("Test info del local:") {
        it("Test nombre del local") { elFortin.nombreLocal shouldBe "El Fortin" }
        it("Test calle del local") { elFortin.direccion.calle shouldBe "Alvarez Jonte" }
        it("Test altura del local") { elFortin.direccion.altura shouldBe 4 }
    }

    describe("Test comprobar medios de pago") {
        it("Test El Fortin medio de pago") { elFortin.comprobarMedioDePago(MedioDePago.EFECTIVO) shouldBe true }
    }
    elFortin.aceptarPuntaje(4)
    elFortin.aceptarPuntaje(3)
    elFortin.aceptarPuntaje(4)
    elFortin.aceptarPuntaje(5)
    elFortin.aceptarPuntaje(2)

    describe("Test de calculos del local:") {
        it("Test puntaje promedio del local") {
            elFortin.esConfiable() shouldBe false
        }
    }
})
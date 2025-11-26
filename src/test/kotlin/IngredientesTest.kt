import algoQuePedir.domain.GrupoAlimenticio
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import algoQuePedir.domain.Ingrediente
import io.kotest.matchers.shouldNotBe

val pan = Ingrediente("pan", 100.00, GrupoAlimenticio.CEREALES_TUBERCULOS, false)
val salchicha = Ingrediente("salchicha", 150.0, GrupoAlimenticio.PROTEINAS, true)
val medallon = Ingrediente("medallon", 250.0, GrupoAlimenticio.PROTEINAS, true)
val salmon = Ingrediente(
    "salmon",
    10.00,
    GrupoAlimenticio.PROTEINAS,
    true
)
val ravioles = Ingrediente(
    "ravioles",
    5.00,
    GrupoAlimenticio.GRASAS_ACEITES,
    false
)
val cafe = Ingrediente(
    "cafe",
    3.00,
    GrupoAlimenticio.AZUCARES_DULCES,
    true
)

class IngredienteTest:DescribeSpec({

    describe("Test info general ingredinte") {
        it("Test nombre") { salmon.nombre shouldBe "salmon" }
        it("Test precio") { salmon.costoMercado shouldBe 10.00 }
        it("Test grupoAlimenticio") { salmon.grupoAlimenticio shouldBe GrupoAlimenticio.PROTEINAS }
        it("Test grupoAlimenticio 2") { salmon.grupoAlimenticio shouldNotBe GrupoAlimenticio.CEREALES_TUBERCULOS }
        it("Test esDeOrigenAnimal") { salmon.esDeOrigenAnimal shouldBe true }
    }
})

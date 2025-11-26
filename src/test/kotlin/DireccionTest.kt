import io.kotest.core.spec.style.DescribeSpec
import algoQuePedir.domain.Direccion
import io.kotest.matchers.shouldBe
import kotlin.math.round
import kotlin.math.roundToInt

val direc1 = Direccion("Cabildo", 1.0,1.0, 100)
val direc2 = Direccion("Rivadavia", 2.0,1.0,2500)
val direccion1 = Direccion("Cabildo", 2.0, 1.0,3)
val direccion2 = Direccion("Rivadavia", 3.0, 1.0,3500)
val direccion3 = Direccion("Cabildo", 200.0, 1.0,9000)
val direccionPizzeria1 = Direccion("Alcorta", 2.0, 1.0,4)
val direccionUser = Direccion("Cabildo", 1.0, 1.0,1)
val direFortin = Direccion(calle = "Alvarez Jonte", coordenadaX = 2.0, 1.0,altura = 4)
val direToscana = Direccion(calle = "Cabildo", coordenadaX = 3.0, 1.0,altura = 5)
val direPeters = Direccion(calle = "Tres de Febrero", coordenadaX = 4.0 , 1.0, altura = 3)

class DireccionTest:DescribeSpec({

    describe("Test basico de direcciones"){
        it("Obtener nombre de las direcciones"){
            direc1.calle shouldBe "Cabildo"
            direc1.altura shouldBe 100
        }
        it("Obtener distancia entre las 2 direcciones"){
            round(direc1.coordenadas.distance(direc2.coordenadas)) shouldBe 111.0
            // Formula usada para calcular distancia : Math.sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))
        }
    }
})
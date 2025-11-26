import com.google.gson.Gson
import algoQuePedir.domain.Ingrediente
import algoQuePedir.domain.GrupoAlimenticio
import algoQuePedir.domain.ServicioIngredientes
import algoQuePedir.domain.repoIngrediente
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ServicioIngredientesTest: DescribeSpec({

        repoIngrediente.coleccion.clear()
        repoIngrediente.currentId = 0
        repoIngrediente.create(pan)
        repoIngrediente.create(salchicha)
        repoIngrediente.create(salmon)
        val ingredientesJson = ServicioIngredientes().getDatos(repoIngrediente)

        val servicio = ServicioIngredientes()

    // Obtener JSON en base a colección
    describe("Test básico de retorno de todos los ingredientes"){
        it("Test formato JSON"){
            ingredientesJson shouldContain "\"id\":"
            ingredientesJson shouldContain "\"nombre\":"
            ingredientesJson shouldContain "\"costoMercado\":"
            ingredientesJson shouldContain "\"grupoAlimenticio\":"
            ingredientesJson shouldContain "\"origenAnimal\":"
        }

        it("Test específico de datos"){
            val listaIngredientes = Gson().fromJson(ingredientesJson, Array<Ingrediente>::class.java).toList()
            listaIngredientes[0].nombre shouldBe "pan"
            listaIngredientes[0].costoMercado shouldBe 100
            listaIngredientes[0].grupoAlimenticio shouldBe GrupoAlimenticio.CEREALES_TUBERCULOS
            listaIngredientes[0].esDeOrigenAnimal shouldBe false
        }
    }

    // Actualizar colección en base al JSON
    describe("Probar actualizar la colección de ingredientes con el JSON"){
        repoIngrediente.coleccion.clear()
        repoIngrediente.getFromService(servicio)

        val ingredienteJson = Ingrediente(
            nombre = "Leche",
            costoMercado = 200.5,
            grupoAlimenticio = GrupoAlimenticio.LACTEOS,
            esDeOrigenAnimal = true
        ).apply { id = 22 }

        it("Ejecutamos la actualización y revisamos que tengamos los objetos"){
            repoIngrediente.coleccion[0] shouldBeEqualToComparingFields  ingredienteJson
        }
        it("Check ID"){
            println(repoIngrediente.getById(3)?.nombre)
            repoIngrediente.coleccion.forEach{ ingrediente ->
                println("ID: ${ingrediente.id}")
                println("Nombre: ${ingrediente.nombre}")
                println("Costo: ${ingrediente.costoMercado}")
                println("Grupo: ${ingrediente.grupoAlimenticio}")
                println("Origen Animal: ${ingrediente.esDeOrigenAnimal}")
            }
            val ids = repoIngrediente.coleccion.map { it.id }
            ids.toSet().size shouldBe ids.size
        }
    }
})

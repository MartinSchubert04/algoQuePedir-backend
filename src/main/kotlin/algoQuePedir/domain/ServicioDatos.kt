package algoQuePedir.domain

import com.google.gson.Gson
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.io.readJson


data class IngredienteJSON(
    val id: Int?,
    val nombre: String,
    val costoMercado: Double,
    val grupoAlimenticio: GrupoAlimenticio,
    val origenAnimal: Boolean
)

abstract class ServicioDatos <T : implementaId> {
    abstract fun getDatos(repositorio: Repositorio<T>): String
}

class ServicioIngredientes: ServicioDatos<Ingrediente>(){
    override fun getDatos(repositorio: Repositorio<Ingrediente>): String {
        val todosIngredientes = repositorio.coleccion

        return Gson().toJson(
            todosIngredientes.map { ingrediente ->
                IngredienteJSON(
                    id = ingrediente.id,
                    nombre = ingrediente.nombre,
                    costoMercado = ingrediente.costoMercado,
                    grupoAlimenticio = ingrediente.grupoAlimenticio,
                    origenAnimal = ingrediente.esDeOrigenAnimal
                )
            })
    }

    fun getIngredientesJSON(): List<Ingrediente>{
        val df = DataFrame.readJson("src/main/kotlin/algoQuePedir/data/Ingredientes.json")
        val listaIngredientes = df.map { ingrediente ->
            Ingrediente(
                nombre = ingrediente["nombre"] as String,
                costoMercado = ingrediente["costo"] as Double,
                grupoAlimenticio = GrupoAlimenticio.fromJsonValue(ingrediente["grupo"].toString()),
                esDeOrigenAnimal = ingrediente["origenAnimal"] as Boolean
            ).apply { id = ingrediente["id"] as Int? }
        }
        return listaIngredientes
    } }
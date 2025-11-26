package algoQuePedir.domain
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Sencillo::class, name = "Sencillo"),
    JsonSubTypes.Type(value = Veganos::class, name = "Veganos"),
    JsonSubTypes.Type(value = Exquisitos::class, name = "Exquisitos"),
    JsonSubTypes.Type(value = Conservador::class, name = "Conservador"),
    JsonSubTypes.Type(value = Fieles::class, name = "Fieles"),
    JsonSubTypes.Type(value = Marketing::class, name = "Marketing"),
    JsonSubTypes.Type(value = Impacientes::class, name = "Impacientes"),
    JsonSubTypes.Type(value = IntercalaEdad::class, name = "IntercalaEdad"),
    JsonSubTypes.Type(value = CondicionY::class, name = "CondicionY")
)

interface CondicionPlatoUsuario {
    fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean
}

object Sencillo : CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean = true
}

object Veganos: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        !plato.ingredientes.any { ingrediente -> ingrediente.esDeOrigenAnimal }
}

object Exquisitos: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        plato.esDeAutor
}

object Conservador: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        plato.ingredientes.all {ingrediente -> ingrediente in usuario.ingredientesPreferidos}
}

object Fieles: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        usuario.localesPreferidos.contains (plato.local)
}

object Marketing: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        usuario.palabrasMarketing.any {palabra -> plato.descripcion.contains(palabra, ignoreCase = true)}
}

object Impacientes: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean =
        usuario.localCercano(plato.local)
}

object IntercalaEdad: CondicionPlatoUsuario {
    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean {
        if(usuario.edad() % 2 == 0){
            return Exquisitos.aceptaPlato(plato, usuario)
        }
        else{
            return Conservador.aceptaPlato(plato, usuario)
        }
    }
}

class CondicionY @JsonCreator constructor(
    @JsonProperty("condiciones")
    val condiciones: MutableList<CondicionPlatoUsuario> = mutableListOf()
) : CondicionPlatoUsuario {

    override fun aceptaPlato(plato: Plato, usuario: Usuario): Boolean {
        return condiciones.all { it.aceptaPlato(plato, usuario) }
    }
}
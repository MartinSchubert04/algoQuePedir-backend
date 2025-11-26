package algoQuePedir.domain

import local.Local
import cupon.Cupon
import local.adminLocal
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

// interface necesario para que se pueda limitar el generic que se pasa al repositorio
interface implementaId {
    var id: Int?
}

abstract class Repositorio<T> where T: implementaId {
    var coleccion = mutableListOf<T>()
    var currentId = 1

    //funcion para que traetodo en la paguina a mostrar
    open fun getAll(): List<T> = coleccion

    fun create(obj: T) {
        if (obj.id == null) {
            obj.id = currentId++
        }
        coleccion.add(obj)
    }

    fun delete(obj: T) {
        val toRemove = coleccion.firstOrNull { it.id == obj.id }
        if (toRemove != null) {
            toRemove.id = null
            coleccion.remove(toRemove)
        } else {
            throw RuntimeException("No se encontró el objeto para eliminar")
        }
    }

    fun update(obj: T) {
        val index = coleccion.indexOfFirst { it.id == obj.id }
        if (index == -1) {
            throw RuntimeException("No se encontró el objeto con id ${obj.id}")
        }
        coleccion[index] = obj
    }

    fun updateById(id: Int, obj: T): T{
        val index = coleccion.indexOfFirst { it.id == id }
        if(index != -1){
            obj.id = coleccion[index].id
            coleccion[index] = obj
            return obj
        }
        throw RuntimeException("No se encontró el objeto con el ID ${obj.id}")
    }

    fun getById(id: Int): T? {
        return coleccion.firstOrNull { it.id == id }
    }

    // defino al filtro como lambda, el boolean indica si se cumple para ese obj particular (T)
    fun buscarPor(filtro: (T) -> Boolean): List<T> {
        return coleccion.filter(filtro) // aplico el filtro a cada obj de la coleccion
    }

    abstract fun <Q> buscar(query: Q): List<T>
}

@Component
object repoPlato: Repositorio<Plato>() {
     override fun <Q> buscar(query: Q) = buscarPor { plato ->
         val q = query.toString()
        plato.descripcion.contains(q, ignoreCase = true) ||
        plato.nombre.contains(q, ignoreCase = true) ||
        plato.local.nombreLocal.contains(q, ignoreCase = true) ||
        plato.local.direccion.calle.equals(q, ignoreCase = true)
    }
}

@Component
object repoUsuario: Repositorio<Usuario>() {
     override fun <Q> buscar(query: Q) = buscarPor { usuario ->
         val q = query.toString()
        usuario.datos.nombre.contains(q, true) ||
        usuario.datos.apellido.contains(q, true) ||
        usuario.datos.username.equals(q, true)
    }
}

@Component
object repoAdminLocal: Repositorio<adminLocal>() {
    override fun <Q> buscar(query: Q) = buscarPor { usuario ->
        val q = query.toString()
        usuario.username.equals(q, true)
    }
}

@Component
object repoIngrediente: Repositorio<Ingrediente>() {

     override fun <Q> buscar(query: Q) = buscarPor { ingrediente ->
         val q = query.toString()
        ingrediente.nombre.equals(q, true)
    }

    fun getFromService(servicio: ServicioIngredientes){
        servicio.getIngredientesJSON().map { ingrediente ->
            ingrediente.id?.let{ id ->
                if(getById(id) == null){
                    create(ingrediente)
                }else{
                    update(ingrediente)
                }
            }
        }
    }
    fun deleteById(id: Int) {
        val ingrediente = getById(id)
        if (ingrediente != null) {
            delete(ingrediente)
        } else {
            throw RuntimeException("No se encontró ingrediente con id $id")
        }
    }
}

@Component
object repoLocal: Repositorio<Local>() {
     override fun <Q> buscar(query: Q) = buscarPor { local ->
         val q = query.toString()
        local.nombreLocal.contains(q, true) ||
        local.direccion.calle.equals(q, true) ||
        local.adminUsername.equals(q, true)
    }
}

@Component
object repoDelivery: Repositorio<Delivery>() {
     override fun <Q> buscar(query: Q) = buscarPor { delivery ->
         val q = query.toString()
        delivery.datos.username.startsWith(q, true)
    }
}

@Component
object repoCupon: Repositorio<Cupon>() {
     override fun <Q> buscar(query: Q) = buscarPor { cupon ->
         cupon.porcentajeBase.equals(query)
    }
}



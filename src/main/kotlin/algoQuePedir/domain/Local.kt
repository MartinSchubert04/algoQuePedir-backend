package local
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.implementaId
import algoQuePedir.domain.Plato
import algoQuePedir.domain.Inbox
import algoQuePedir.errors.BusinessException


class adminLocal(
    val username: String,
    val password: String,
    var local: Local? = null
): implementaId {
    override var id: Int? = null
    fun validate(username: String, password: String) = username == this.username && password == this.password

    fun validarRegistro(passConfirmation: String) {
      when {
          this.username.isBlank() || this.password.isBlank() || passConfirmation.isBlank() -> throw BusinessException("No puede haber campos vacios")
          this.password != passConfirmation -> throw BusinessException("Las contraseñas no coinciden")
      }
    }
}

class Local constructor (
    var nombreLocal: String,
    var direccion: Direccion,
    var porcentajeBeneficio: Double,
    var porcentajeAutor: Double? = null,
    var mediosDePago: MutableList<MedioDePago> = mutableListOf(),
    var pendiente:Boolean=false,
    var menu: MutableList<Plato> = mutableListOf(),  // Terminar de implementar el menu
    var imgURL: String = ""
    ): implementaId {

    lateinit var adminUsername: String


    override var id: Int? = null

    var listaPedidos: MutableList<Pedido> = mutableListOf()
    var puntajes: MutableList<Int> = mutableListOf()
    var inbox = Inbox()

    fun cantidadPuntajes() = puntajes.size
    fun promedioPuntajes(): Double {
        if (puntajes.size == 0) return 0.0
        return puntajes.sum().toDouble() / this.cantidadPuntajes()
    }

    fun cantidadPedidos() = listaPedidos.size

    fun comprobarMedioDePago(unPago: MedioDePago): Boolean {
        return mediosDePago.contains(unPago)
    }

    fun aceptarPuntaje(puntaje: Int) {
        puntajes.add(puntaje)
    }

    fun esConfiable(): Boolean{
        if(puntajes.size > 0){
            return puntajes.sum().toDouble() / puntajes.size in 4.0..5.0
        }else throw RuntimeException("La lista de pedidos esta vacia")
    }

    fun tieneCuponAplicado(unPedido: Pedido) = unPedido.cupon != null

    fun agregarPedido(unPedido: Pedido){ //asigna id al pedido
        var candidatoId = 1
        while (listaPedidos.any { it.id == candidatoId }) {
            candidatoId++
        }
        unPedido.id = candidatoId
        listaPedidos.add(unPedido)
    }

    fun agregarPlato(unPlato: Plato){
        menu.add(unPlato)
    }

    fun actualizar(localActualizado: Local) {
        this.nombreLocal = localActualizado.nombreLocal
        this.direccion = localActualizado.direccion
        this.porcentajeBeneficio = localActualizado.porcentajeBeneficio
        this.porcentajeAutor = localActualizado.porcentajeAutor ?: 0.0
        this.imgURL = localActualizado.imgURL
        this.mediosDePago = localActualizado.mediosDePago
    }

    fun validar() {
        when {
            nombreLocal.isBlank() || direccion.calle.isBlank() ||
            imgURL.isBlank()  || porcentajeAutor == null -> throw BusinessException("No puede haber campos vacios")

            porcentajeBeneficio < 0 || direccion.altura < 0 ||
            porcentajeAutor!! < 0 -> throw BusinessException("No se deben ingresar valores negativos")

            mediosDePago.isEmpty() -> throw BusinessException("Debe contar con almenos un medio de pago")
        }
    }
}




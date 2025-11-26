package algoQuePedir.domain

import cupon.Cupon
import local.Local
import java.time.LocalDateTime

class Pedido (
    val cliente: Usuario,
    val local: Local,
    var platos: MutableList<Plato> = mutableListOf(),
    var medioDePago: MedioDePago,
    var estadoDelPedido: EstadoDelPedido = EstadoDelPedido.PENDIENTE,
    val momentoDeOrden: LocalDateTime = LocalDateTime.now(),
    var cupon: Cupon? = null,
){
    var delivery: Delivery? = null
    var id: Int? = null //agregado id del pedido, para utilizarlo en la llamada al back
    fun agregarPlato(platoNuevo: Plato){
        if (cliente.filtrarPorIngrediente(platoNuevo) &&
            platoNuevo.local == local
            ){
            platos.add(platoNuevo)
        } else throw RuntimeException("No se pudo agregar el plato")
    }
    fun esCertificado():Boolean {
        return (cliente.tiempoRegistrado() >= 1 && local.esConfiable())
    }

    fun totalAPagar(): Double {
        return if (cupon != null) {
            ((sumaPlatos() + costeDeEnvio()) * adicionalPorMedio()) - cupon!!.descuentoTotal(this)
        } else {
            (sumaPlatos() + costeDeEnvio()) * adicionalPorMedio()
        }
    }

    fun costeDeEnvio(): Double {
        return platos.sumOf{it.precioSinDescuentos()} * 0.1
    }
    fun adicionalPorMedio(): Double {
        if (medioDePago == MedioDePago.EFECTIVO) {
            return 1.0
        } else return 1.05
    }

    fun aceptarDelivery(posibleDelivery: Delivery){
        if (posibleDelivery.puedeAceptarPedido(this)){
            delivery = posibleDelivery
        }
        else throw RuntimeException("No se pudo agregar el delivery")
    }

    fun sumaPlatos() = platos.sumOf { it.precioFinal() * (it.cantidad ?: 1) }
}
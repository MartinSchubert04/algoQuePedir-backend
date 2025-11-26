package algoQuePedir.dto

import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Plato
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import kotlin.math.roundToLong
fun Double.redondearADosDecimales(): Double {
    return if (this.isNaN() || this.isInfinite()) {
        this } else {(this * 100).roundToLong() / 100.0}}

    data class UserPlatoDTO(
        val id: Int,
        val nombrePlato: String,
        val cantidad: Int,
        val costo: Double,
        val imagenPlato: String,
    )

    data class UserPedidoDTO(
        val id: Int,
        val estado: EstadoDelPedido,
        val nombreLocal: String,
        val valoracion: Double,
        val distanciaALocal: Double,
        val fecha: String,
        val fotoLocal: String,
        val items: Int,
        val total: Double,
        val metodoPago: MedioDePago,
        val platos: List<UserPlatoDTO>,
        val costeEnvio: Double,
        val adicionalPorMedio: Double,
    )


    fun Pedido.toUserDTO(): UserPedidoDTO {
        val formatoDiaYMes = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale.forLanguageTag("es-ES"))
        return UserPedidoDTO(
            id = id ?: hashCode(), //damos con el id, sino un hash
            estado = estadoDelPedido,
            nombreLocal = local.nombreLocal,
            valoracion = local.puntajes.average().redondearADosDecimales(),
            distanciaALocal = (cliente.ubicacion.coordenadas.distance(local.direccion.coordenadas)/1000).redondearADosDecimales(),
            fecha = momentoDeOrden.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).format(formatoDiaYMes),
            fotoLocal = local.imgURL,
            items = platos.size,
            total = totalAPagar().redondearADosDecimales(),
            metodoPago = medioDePago,
            platos = platos.map { it.toUserDTO() },
            costeEnvio = this.costeDeEnvio().redondearADosDecimales(),
            adicionalPorMedio = this.adicionalPorMedio().redondearADosDecimales(),
        )
    }

    fun Plato.toUserDTO(): UserPlatoDTO  = UserPlatoDTO(
        id = id ?: hashCode(),
        nombrePlato = nombre,
        cantidad = cantidad ?: 0,
        costo = precioFinal(),
        imagenPlato = imagen,
    )
package algoQuePedir.dto

import algoQuePedir.domain.*
import local.Local
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PlatoDTO(
    val id: Int,
    val nombrePlato: String,
    val descripcion: String,
    val cantidad: Int,
    val costo: Double,
    val imagenPlato: String,
    val ingredientes: List<Ingrediente>,
    val valorBase: Double,
)

data class PedidoDTO(
    val id: Int,
    val estado: EstadoDelPedido,
    val usuario: String,
    val nombreCliente: String,
    val apellidoCliente: String,
    val foto: String,
    val hora: String,
    val items: Int,
    val total: Double,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val metodoPago: MedioDePago,
    val platos: List<PlatoDTO>,
    val costeEnvio: Double,
    val adicionalPorMedio: Double,

    val nombreLocal: String,
    val direccionLocal: String,
    val fecha: String,
)

data class PedidoCheckoutDTO(
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
    val platos: List<PlatoCheckoutDTO>,
    val costeEnvio: Double,
    val adicionalPorMedio: Double
)

data class PlatoCheckoutDTO(
    val id: Int,
    val nombrePlato: String,
    val cantidad: Int,
    val costo: Double,
    val imagenPlato: String
)


fun Pedido.toDTO(): PedidoDTO {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("es-ES"))
    val formatoDiaYMes = DateTimeFormatter.ofPattern("dd/MM", Locale.forLanguageTag("es-ES"))
    return PedidoDTO(
        id = id ?: hashCode(), //damos con el id, sino un hash
        estado = estadoDelPedido,
        nombreCliente = cliente.datos.nombre,
        apellidoCliente = cliente.datos.apellido,
        usuario = cliente.datos.username,
        foto = cliente.imagen,
        hora = momentoDeOrden.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).format(formatter),
        items = platos.size,
        total = totalAPagar(),
        direccion = cliente.ubicacion.calle,
        latitud = cliente.ubicacion.coordenadaX,
        longitud = cliente.ubicacion.altura.toDouble(),
        metodoPago = medioDePago,
        platos = platos.map { it.toDTO() },
        costeEnvio = this.costeDeEnvio(),
        adicionalPorMedio = this.adicionalPorMedio(),

        //usuario
        nombreLocal = local.nombreLocal,
        direccionLocal = local.direccion.calle,
        fecha = momentoDeOrden.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).format(formatoDiaYMes),
        )
}

fun Plato.toDTO(): PlatoDTO  = PlatoDTO(
    id = id ?: hashCode(),
    nombrePlato = nombre,
    descripcion = descripcion,
    cantidad = cantidad ?: 0,
    costo = precioFinal(),
    imagenPlato = imagen,
    ingredientes = this.ingredientes,
    valorBase = this.valorBase
)

data class BasicPedidoData(
    val local: String,
    val platos: List<String>,
    val metodoPago: MedioDePago
)

data class PlatoUpdateDTO(
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val valorBase: Double,
    val esDeAutor: Boolean,
    val ingredientes: MutableList<Ingrediente>,
    val descuento: Double,
    val porcentajeRegalia: Double,
    val fechaLanzamiento: LocalDateTime? = null
)

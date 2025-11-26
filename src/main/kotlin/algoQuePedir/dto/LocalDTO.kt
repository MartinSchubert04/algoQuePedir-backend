package algoQuePedir.dto

import algoQuePedir.domain.Delivery
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.Inbox
import algoQuePedir.domain.MedioDePago
import algoQuePedir.domain.Pedido
import local.Local
import java.awt.Menu

data class LocalDTO (
    val id: Int? = null,
    val nombreLocal: String,
    val imgURL: String,
    val calle: String,
    val latitud: Double,
    val longitud: Double,
    val altura: Int,
    val porcentajeComision: Double,
    val porcentajeAutor: Double?,
    var mediosDePago: MutableList<MedioDePago> = mutableListOf(),
    )

data class ResumenPedidoDTO(
    val recargo: Double,
    val costeEnvio: Double,
    val total: Double
)

fun Local.toDTO(): LocalDTO = LocalDTO(
    id = id,
    nombreLocal = nombreLocal,
    imgURL = imgURL,
    calle =  direccion.calle,
    latitud = direccion.coordenadas.x,
    longitud = direccion.coordenadas.y,
    altura = direccion.altura,
    porcentajeComision =  porcentajeBeneficio,
    porcentajeAutor= porcentajeAutor,
    mediosDePago = mediosDePago
)

fun LocalDTO.toEntity(): Local = Local(
    nombreLocal = nombreLocal,
    direccion = Direccion(calle, latitud, longitud, altura),
    porcentajeBeneficio = porcentajeComision,
    porcentajeAutor = porcentajeAutor,
    mediosDePago = mediosDePago,
    imgURL = imgURL
).apply {
    id = this@toEntity.id
}

data class LocalDetalleDTO(
    val id: Int? = null,
    val nombreLocal: String,
    val imgURL: String,
    val calle: String,
    val altura: Int,
    var menu: MutableList<PlatoDTO> = mutableListOf(),
    var mediosDePago: MutableList<MedioDePago> = mutableListOf(),
    var promedioPuntajes: Double,
    var cantidadPuntajes: Int,
    var cantidadPedidos: Int,
    val inbox: Inbox
)

fun Local.toDetalleDTO(): LocalDetalleDTO = LocalDetalleDTO(
    id = id,
    nombreLocal = nombreLocal,
    imgURL = imgURL,
    calle =  direccion.calle,
    altura = direccion.altura,
    mediosDePago = mediosDePago,
    menu = menu.map { it.toDTO() }.toMutableList(),
    promedioPuntajes = this.promedioPuntajes(),
    cantidadPuntajes = this.cantidadPuntajes(),
    cantidadPedidos = this.cantidadPedidos(),
    inbox = inbox
)

data class LocalSearchDTO(
    val id: Int? = null,
    val nombreLocal: String,
    val imgURL: String,
    val calle: String,
    val altura: Int,
)

fun Local.toSearchDTO(): LocalSearchDTO = LocalSearchDTO(
    id = id,
    nombreLocal = nombreLocal,
    imgURL = imgURL,
    calle =  direccion.calle,
    altura = direccion.altura,
    )

data class LocalPerfilDTO(
    val nombreLocal: String,
    val imgPath: String,
    val distancia: Double,
    val promedio: Double,
    val tipoDelivery: String,
    val mediosDePago: List<MedioDePago>
)
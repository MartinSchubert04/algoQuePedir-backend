package algoQuePedir.dto

import algoQuePedir.domain.Direccion
import local.Local


data class CalificacionDTO(
    val localId: Int,
    val puntaje: Int,
)
data class CalificacionesResponse(
    val id: Int? = null,
    val nombreLocal: String,
    val imagen: String,
    val distancia: Int,
    val promedio: Int,
)


fun Local.calificacionesToDto(distancia: Int, promedio: Int): CalificacionesResponse =
    CalificacionesResponse(
        id = id,
        nombreLocal = nombreLocal,
        imagen = imgURL,
        distancia = distancia,
        promedio = promedio
    )
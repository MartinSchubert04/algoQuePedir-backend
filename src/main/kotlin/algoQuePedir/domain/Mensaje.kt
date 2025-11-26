package algoQuePedir.domain

import java.time.LocalDateTime

data class Mensaje(
    val fecha: LocalDateTime = LocalDateTime.now(),
    val asunto: String,
    val contenido: String,
    var leido : Boolean
)
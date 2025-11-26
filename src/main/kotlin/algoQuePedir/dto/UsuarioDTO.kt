package algoQuePedir.dto

import algoQuePedir.domain.*


data class UserRegisterRequest(
    val username: String,
    val pass: String,
    val passConfirmation: String
)

data class UserLoginRequest(
    val username: String,
    val pass: String
)

data class UserUpdateDTO(
    val nombre: String,
    val apellido: String,
    val direccion: String,
    val altura: Int,
    val latitud: Double,
    val longitud: Double,
    val condicion: CondicionPlatoUsuario,
    val ingredientesEvitar: MutableList<String>,    // Son solamente los nombres
    val ingredientesPreferidos: MutableList<String>, // Son solamente los nombres
    val restosFavoritos: MutableList<LocalPerfilDTO>,
    val palabrasMarketing: MutableList<String>,
    val distanciaMax: Double,
)

data class UserWithPedidosDTO(
    val id: Int?,
    val nombre: String,
    val pedidos: List<BasicPedidoData>
)
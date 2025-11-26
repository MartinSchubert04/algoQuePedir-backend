package algoQuePedir.dto

data class LoginResponse(
    val username: String,
    val localId: Int?,
    val localNombre: String?
)

data class UserLoginResponse(
    val username: String,
    val userId: Int?,
)
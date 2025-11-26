package algoQuePedir.service

import algoQuePedir.domain.DatosPersonales
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.Usuario
import algoQuePedir.errors.BusinessException
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.UserLoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import algoQuePedir.dto.UserLoginResponse
import algoQuePedir.dto.UserRegisterRequest

@Service
class UserAuthService(val repository: repoUsuario) {

    fun register(request: UserRegisterRequest): String {
        val usersHallados = repository.buscar(request.username)

        if (usersHallados.isNotEmpty()) throw BusinessException("El usuario ingresado ya existe")
        val defaultDatos = DatosPersonales("Nuevo","",request.username, request.pass)
        val defaultUbicacion = Direccion("Sin dirección", 0.0, 0.0,0)
        val user = Usuario(defaultDatos,"",defaultUbicacion )

        user.validarRegistro(request.passConfirmation)

        repository.create(user)

        return user.datos.username
    }

    fun login(request: UserLoginRequest): UserLoginResponse {
        val encontrados = repository.buscar(request.username)
        val user = encontrados.firstOrNull()

        if (user != null && user.validate(request.username, request.pass)) {
            val userId = user.id
            val userNombre = user.datos.username
            return UserLoginResponse(userNombre, userId)
        } else {
            throw BusinessException("Usuario o contraseña incorrectos")
        }
    }

}

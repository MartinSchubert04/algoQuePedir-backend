package algoQuePedir.service

import algoQuePedir.errors.BusinessException
import algoQuePedir.domain.repoAdminLocal
import local.adminLocal
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import algoQuePedir.dto.LoginResponse

@Service
class AuthService(val repository: repoAdminLocal) {

    fun register(user: adminLocal, passConfirmation: String): String {
        val usersHallados = repository.buscar(user.username)

        if (usersHallados.isNotEmpty()) throw BusinessException("El usuario ingresado ya existe")
        user.validarRegistro(passConfirmation)

        repository.create(user)

        return user.username
    }

    fun login(username: String, password: String): LoginResponse {
        val encontrados = repository.buscar(username)
        val user = encontrados.firstOrNull()

        if (user != null && user.validate(username, password)) {
            val localId = user.local?.id
            val localNombre = user.local?.nombreLocal
            return LoginResponse(user.username, localId, localNombre)
        } else {
            throw BusinessException("Usuario o contraseña incorrectos")
        }
    }

}

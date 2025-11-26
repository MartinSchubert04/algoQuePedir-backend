package algoQuePedir.controller

import algoQuePedir.service.AuthService
import algoQuePedir.domain.repoAdminLocal
import local.adminLocal
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import algoQuePedir.dto.LoginResponse

data class RequestRegister (
    val user: adminLocal,
    val passConfirmation: String
)


@RestController
@CrossOrigin("*")
class AuthController(val repository: repoAdminLocal, val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody body: RequestRegister): ResponseEntity<String> {
        return ResponseEntity.ok(authService.register(body.user, body.passConfirmation))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: adminLocal): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(authService.login(body.username, body.password))
    }
}
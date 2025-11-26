package algoQuePedir.controller

import algoQuePedir.service.UserAuthService
import algoQuePedir.domain.repoUsuario
import algoQuePedir.domain.Usuario
import algoQuePedir.dto.UserRegisterRequest
import algoQuePedir.dto.UserLoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import algoQuePedir.dto.UserLoginResponse

@RestController
@CrossOrigin("*")
class UserAuthController(val repository: repoUsuario, val userAuthService: UserAuthService) {

    @PostMapping("/user/register")
    fun register(@RequestBody body: UserRegisterRequest): ResponseEntity<String> {
        return ResponseEntity.ok(userAuthService.register(body))
    }

    @PostMapping("/user/login")
    fun login(@RequestBody body: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        return ResponseEntity.ok(userAuthService.login(body))
    }
}
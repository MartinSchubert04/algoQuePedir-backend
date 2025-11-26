package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.DatosPersonales
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.Usuario
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.UserRegisterRequest
import algoQuePedir.dto.UserLoginRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de autenticación de Usuarios")
class UserAuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: repoUsuario

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun init() {
        repository.coleccion.clear()
    }

    // --- Tests para POST /user/register ---

    @Test
    @DisplayName("POST /user/register crea un nuevo usuario exitosamente")
    fun `registrar usuario exitoso`() {
        val registerRequest = UserRegisterRequest(
            username = "newUser",
            pass= "123",
            passConfirmation = "123"
        )
        val jsonRequest = objectMapper.writeValueAsString(registerRequest)

        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("newUser"))

        val usersInRepo = repository.buscar("newUser")
        assertEquals(1, usersInRepo.size)
        assertEquals("newUser", usersInRepo.first().datos.username)
    }

    @Test
    @DisplayName("POST /user/register falla si las contraseñas no coinciden")
    fun `Contrasena de usuario registrada incorrecta`() {
        val registerRequest = UserRegisterRequest(
            username = "failUser",
            pass = "123",
            passConfirmation = "456"
        )
        val jsonRequest = objectMapper.writeValueAsString(registerRequest)

        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Las contraseñas no coinciden"))
    }

    @Test
    @DisplayName("POST /user/register falla si el usuario ya existe")
    fun `registrar usuario ya existe`() {
        val registerRequest = UserRegisterRequest(
            username = "existeUser",
            pass = "123",
            passConfirmation = "123"
        )
        val jsonRequest = objectMapper.writeValueAsString(registerRequest)

        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("El usuario ingresado ya existe"))
    }

    @Test
    @DisplayName("POST /user/register falla si un campo esta vacio")
    fun `campo vacio`() {
        val registerRequest = UserRegisterRequest(
            username = "",
            pass = "123",
            passConfirmation = "123"
        )
        val jsonRequest = objectMapper.writeValueAsString(registerRequest)

        mockMvc.perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("No puede haber campos vacios"))
    }

    // --- Tests para POST /user/login ---

    @Test
    @DisplayName("POST /user/login retorna 200 OK y el DTO de respuesta")
    fun `inicio de sesión exitoso del usuario`() {
        val defaultDatos = DatosPersonales(
            nombre = "Login", apellido = "User",
            username = "loginUser", password = "123"
        )
        val defaultUbicacion = Direccion(
            calle = "Sin dirección", coordenadaX = 0.0,
            altura = 0, coordenadaY = 0.0
        )
        val existeUser = Usuario(
            datos = defaultDatos,
            fechaNacimiento = "20000101",
            ubicacion = defaultUbicacion
        )
        repository.create(existeUser)

        val loginRequest = UserLoginRequest(username = "loginUser", pass = "123")
        val jsonRequest = objectMapper.writeValueAsString(loginRequest)

        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("loginUser"))
            .andExpect(jsonPath("$.userId").value(existeUser.id))
    }

    @Test
    @DisplayName("POST /user/login falla con contraseña incorrecta")
    fun `iniciar sesión usuario contraseña incorrecta`() {
        val defaultDatos = DatosPersonales(
            nombre = "Login", apellido = "User",
            username = "loginUser", password = "123"
        )
        val defaultUbicacion = Direccion(
            calle = "Sin dirección", coordenadaX = 0.0,
            altura = 0, coordenadaY = 0.0
        )
        repository.create(Usuario(
            datos = defaultDatos,
            fechaNacimiento = "20000101",
            ubicacion = defaultUbicacion
        ))

        val loginRequest = UserLoginRequest(username = "loginUser", pass = "passincorrecta")
        val jsonRequest = objectMapper.writeValueAsString(loginRequest)

        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Usuario o contraseña incorrectos"))
    }

    @Test
    @DisplayName("POST /user/login falla si el usuario no existe")
    fun `usuario no encontrado`() {
        val loginRequest = UserLoginRequest(username = "noUser", pass = "123")
        val jsonRequest = objectMapper.writeValueAsString(loginRequest)

        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Usuario o contraseña incorrectos"))
    }
}
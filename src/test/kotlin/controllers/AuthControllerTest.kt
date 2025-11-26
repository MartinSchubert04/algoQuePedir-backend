package controllers

import com.fasterxml.jackson.databind.ObjectMapper
import algoQuePedir.domain.repoAdminLocal
import local.adminLocal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import algoQuePedir.App
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import algoQuePedir.bootstrap.AppBootstrap

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de autenticacion")
class AuthControllerTest {

        @Autowired
        private lateinit var mockMvc: MockMvc

        @Autowired
        private lateinit var repository: repoAdminLocal

        lateinit var MOCK_USER_JSON: String
        lateinit var MOCK_USER_JSON_LOGIN: String
        lateinit var MOCK_USER_JSON_LOGIN_ERROR: String



        // --- POST /register Success Test ---

        @BeforeEach
        fun init() {
            repository.coleccion.clear()



            MOCK_USER_JSON = """
                {
                    "user": {
                        "username": "testAdmin",
                        "password": "testPass"
                    },
                    "passConfirmation": "testPass"
                }
            """.trimIndent()

            MOCK_USER_JSON_LOGIN = """
                     {
                        "username": "testAdmin",
                        "password": "testPass"
                    }
            """.trimIndent()

            MOCK_USER_JSON_LOGIN_ERROR = """
                     {
                        "username": "testAdmin",
                        "password": "asd123"
                    }
            """.trimIndent()
        }

        @Test
        fun `POST register returns 200 OK y username cuan`() {
            val MOCK_USER = adminLocal(
                username = "testAdmin",
                password = "testPass",
            )


            mockMvc.perform(
                post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(MOCK_USER_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(content().string(MOCK_USER.username))
        }

        // --- POST /register Conflict Test ---

        @Test
        fun `POST register returns 409 Conflict when user already exists`() {
            val MOCK_USER = adminLocal(
                username = "testAdmin",
                password = "testPass",
            )
            // Registrar primero
            mockMvc.perform(
                post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(MOCK_USER_JSON)
            )

            // Intentar registrar el mismo
            mockMvc.perform(
                post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(MOCK_USER_JSON)
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().string("El usuario ingresado ya existe"))
        }

            @Test
            fun `POST login exitoso`() {
                // registramos al usuario
                mockMvc.perform(
                    post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MOCK_USER_JSON)
                )
                    .andExpect(status().isOk)

                // logueamos
                mockMvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MOCK_USER_JSON_LOGIN)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.username").value("testAdmin"))
            }

            @Test
            fun `POST login retorna error cuando las credenciales no coinciden`() {

                // registramos al usuario
                mockMvc.perform(
                    post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MOCK_USER_JSON)
                )
                    .andExpect(status().isOk)



                mockMvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MOCK_USER_JSON_LOGIN_ERROR)
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().string("Usuario o contraseña incorrectos"))
            }
}
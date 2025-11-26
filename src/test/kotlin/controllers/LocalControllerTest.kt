package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.controller.LocalController
import algoQuePedir.controller.LocalRequest
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.MedioDePago
import algoQuePedir.dto.LocalDTO
import algoQuePedir.errors.BusinessException
import algoQuePedir.service.LocalService
import com.fasterxml.jackson.databind.ObjectMapper
import algoQuePedir.domain.repoLocal
import algoQuePedir.dto.toDTO
import local.Local
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.collections.mutableListOf

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
class LocalControllerTest {


        @Autowired
        private lateinit var mockMvc: MockMvc

        @Autowired
        private lateinit var repoLocal: repoLocal

        @Autowired
        private lateinit var objectMapper: ObjectMapper

        private val ADMIN_USER = "testAdmin"

        private val MOCK_LOCAL = Local(
            nombreLocal = "Pizzeria Test",
            direccion = Direccion("Siempre Viva", -58.3816, 1.0, 100),
            porcentajeBeneficio = 0.1
        ).apply { adminUsername = ADMIN_USER }




        @BeforeEach
        fun setup() {
            repoLocal.coleccion.clear()
            MOCK_LOCAL.id = 0

            repoLocal.create(MOCK_LOCAL)
        }

        @Test
        fun `getLocalActual retorna Local cuando existe`() {
            mockMvc.perform(get("/local")
                .param("user", ADMIN_USER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nombreLocal").value(MOCK_LOCAL.nombreLocal))
                .andExpect(jsonPath("$.calle").value(MOCK_LOCAL.direccion.calle))
        }

        @Test
        fun `crear Local retorna LocalDTO`() {
            val request = LocalRequest(
                local = LocalDTO(
                    nombreLocal = "Pizza Nueva",
                    imgURL = "url/img.jpg",
                    calle = "Calle Falsa",
                    longitud = 123.0,
                    latitud = 10.0,
                    altura = 10,
                    porcentajeComision = 0.2,
                    porcentajeAutor = 0.1,
                    mediosDePago = mutableListOf(MedioDePago.TRANSFERENCIA)
                ),
                adminName = ADMIN_USER
            )

            val jsonBody = objectMapper.writeValueAsString(request)

            mockMvc.perform(post("/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nombreLocal").value("Pizza Nueva"))
                .andExpect(jsonPath("$.calle").value("Calle Falsa"))

        }

        @Test
        fun `actualizar Local retorna LocalDTO actualizado`() {
            val request = LocalRequest(
                local = LocalDTO(
                    nombreLocal = "Pizza Nueva",
                    imgURL = "url/img.jpg",
                    calle = "Calle Falsa",
                    longitud = 123.0,
                    latitud = 10.0,
                    altura = 10,
                    porcentajeComision = 0.2,
                    porcentajeAutor = 0.1,
                    mediosDePago = mutableListOf(MedioDePago.TRANSFERENCIA)
                ),
                adminName = ADMIN_USER
            )

            val jsonBody1 = objectMapper.writeValueAsString(request)

            val result = mockMvc.perform(post("/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody1))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nombreLocal").value("Pizza Nueva"))
                .andExpect(jsonPath("$.calle").value("Calle Falsa"))
                .andReturn()

            val createdLocal = objectMapper.readValue(result.response.contentAsString, LocalDTO::class.java)
            val localId = createdLocal.id!!

            val updatedLocal = LocalDTO(
                id = localId,
                nombreLocal = "Pizzeria Actualizada",
                imgURL = "url/img.jpg",
                calle = "Siempre Viva",
                longitud = -58.3816,
                latitud = -34.6037,
                altura = 742,
                porcentajeComision = 10.0,
                porcentajeAutor = 5.0,
                mediosDePago = mutableListOf(MedioDePago.TRANSFERENCIA)
            )

            val jsonBody2 = objectMapper.writeValueAsString(updatedLocal)

            mockMvc.perform(put("/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody2))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nombreLocal").value("Pizzeria Actualizada"))
                .andExpect(jsonPath("$.calle").value("Siempre Viva"))
        }

        @Test
        fun `get local por id retorna Local`() {
            mockMvc.perform(get("/local/${MOCK_LOCAL.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nombreLocal").value(MOCK_LOCAL.nombreLocal))
                .andExpect(jsonPath("$.calle").value(MOCK_LOCAL.direccion.calle))
        }
}
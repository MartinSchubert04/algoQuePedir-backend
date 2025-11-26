package controllers

import com.fasterxml.jackson.databind.ObjectMapper
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
import algoQuePedir.domain.*
import algoQuePedir.dto.LocalDTO
import local.Local
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.hasSize

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de busqueda")
class SearchControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Autowired
    private lateinit var repository: repoLocal

    @Autowired
    private lateinit var userRepository: repoUsuario

    private val ADMIN_USER = "testAdmin"




    @BeforeEach
    fun init() {
        // Limpiar y cargar datos de prueba en el repositorio
        repository.coleccion.clear()


        val local1 = Local(
            nombreLocal = "Pizza Ya",
            direccion = Direccion("Cabildo", 20.0, 3.0, 100),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
        ).apply {
            adminUsername = "test"
            imgURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKtCWoQOzlUBun9VGIO2HG1KSoofc2emPLwg&s"
        }

        val local2 = Local(
            nombreLocal = "El fortin",
            direccion = Direccion("Cabildo", 20.0, 3.0, 200),
            porcentajeBeneficio = 0.2,
            mediosDePago = mutableListOf(MedioDePago.EFECTIVO),
        ).apply {
            adminUsername = "test"
            imgURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKtCWoQOzlUBun9VGIO2HG1KSoofc2emPLwg&s"
        }

        repository.create(local1)
        repository.create(local2)
    }

    @Test
    @DisplayName("GET /search devuelve los locales con el nombre indicado cuando no se le pasa un user id")
    fun `buscar por nombre`() {


        mockMvc.perform(
            get("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreLocal", "Pizza")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            // El primero debería ser el más cercano (altura 100 antes que 200)
            .andExpect(jsonPath("$[0].altura").value(100))
    }

    @Test
    @DisplayName("GET /search devuelve los locales ordenados por cercanía al usuario cuando se le pasa un id al body")
    fun `buscar locales cercanos`() {
        val cliente1 = Usuario (
            datos = DatosPersonales(
                nombre = "Agustin",
                apellido = "Gutierrez",
                username = "guti12",
                password = "123abc"
            ),
            fechaNacimiento = "01012010",
            ubicacion = Direccion("Cabildo", 2.0, 1.0,3),
        )
        userRepository.create(cliente1)

        mockMvc.perform(
            get("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "${cliente1.id}")
                .param("nombreLocal", "")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            // El primero debería ser el más cercano (altura 100 antes que 200)
            .andExpect(jsonPath("$[0].altura").value(100))
    }
}


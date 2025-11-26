package controllers

import algoQuePedir.App
import algoQuePedir.bootstrap.AppBootstrap
import algoQuePedir.domain.GrupoAlimenticio
import algoQuePedir.domain.Ingrediente
import algoQuePedir.domain.repoIngrediente
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder

@SpringBootTest(classes = [App::class])
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = [AppBootstrap::class])
@DisplayName("Dado un controller de ingredientes")
class IngredientesControllerTest (@Autowired val mockMvc: MockMvc, @Autowired private val mockMvcBuilder: DefaultMockMvcBuilder) {

    @Autowired
    lateinit var repository: repoIngrediente

    val ingredienteMock = Ingrediente(
        nombre = "CARNE",
        costoMercado = 100.5,
        GrupoAlimenticio.PROTEINAS,
        esDeOrigenAnimal = true
    )

    @BeforeEach
        fun init() {
        repository.coleccion.clear()
        repository.create(ingredienteMock)
    }

    @Test
    fun `Llamado get para obtener todos los ingredientes`(){
        mockMvc
            .perform(MockMvcRequestBuilders.get("/ingrediente"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                        [
                          {
                            "nombre": "${ingredienteMock.nombre}",
                            "costoMercado": ${ingredienteMock.costoMercado},
                            "esDeOrigenAnimal": ${ingredienteMock.esDeOrigenAnimal},
                            "id": ${ingredienteMock.id},
                            "grupoAlimenticio": "${ingredienteMock.grupoAlimenticio}"
                          }
                        ]
        """
                )
            )
    }

    @Test
    fun `Llamado para obtener un ingrediente por ID`(){
        mockMvc
            .perform(MockMvcRequestBuilders.get("/ingrediente/{id}", ingredienteMock.id))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                          {
                            "nombre": "${ingredienteMock.nombre}",
                            "costoMercado": ${ingredienteMock.costoMercado},
                            "esDeOrigenAnimal": ${ingredienteMock.esDeOrigenAnimal},
                            "id": ${ingredienteMock.id},
                            "grupoAlimenticio": "${ingredienteMock.grupoAlimenticio}"
                          }
        """
                )
            )
    }

    @Test
    fun `Creacion de un ingrediente`() {
        // Crear un nuevo ingrediente (acá deberías enviar un body JSON real)
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/ingrediente")
                    .contentType("application/json")
                    .content(
                        """
                    {
                        "nombre": "PAPA",
                        "costoMercado": 50.0,
                        "grupoAlimenticio": "CEREALES_TUBERCULOS",
                        "esDeOrigenAnimal": false
                    }
                    """
                    )
            )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Consultar y verificar que haya 2 ingredientes en total
        mockMvc
            .perform(MockMvcRequestBuilders.get("/ingrediente"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
    }
}
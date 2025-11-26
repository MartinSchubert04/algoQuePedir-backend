package algoQuePedir.controller

import algoQuePedir.domain.repoLocal
import algoQuePedir.dto.CalificacionDTO
import algoQuePedir.dto.CalificacionesResponse
import algoQuePedir.dto.calificacionesToDto
import algoQuePedir.service.LocalService
import algoQuePedir.service.UserService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController



@RestController
@CrossOrigin("*")
class CalificacionController(val repository: repoLocal, val localonservice: LocalService, val userService: UserService) {

    @GetMapping("/calificaciones/{id}")
    fun getLocalesParaCalificar(@PathVariable id: Int): List<CalificacionesResponse> {
        val locales = userService.localesParaCalificar(id)

        return locales.map { local ->
            local.calificacionesToDto(
                distancia = userService.estrellaDistancia(),
                promedio = local.puntajes.average().toInt()
            )
        }
    }

    @PatchMapping("/calificaciones")
    fun calificarLocal(@RequestBody body: CalificacionDTO) {
        localonservice.aceptarPuntaje(body.localId, body.puntaje)
    }

}


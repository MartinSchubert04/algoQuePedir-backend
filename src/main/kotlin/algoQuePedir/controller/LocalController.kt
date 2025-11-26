package algoQuePedir.controller

import algoQuePedir.service.LocalService
import local.Local
import org.springframework.web.bind.annotation.PathVariable
import algoQuePedir.domain.repoLocal
import algoQuePedir.dto.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping


data class LocalRequest(
    val local: LocalDTO,
    val adminName: String
)

@RestController
@CrossOrigin("*")
class LocalController(val repository: repoLocal, val localService: LocalService) {

    @GetMapping("/local")
    fun getLocalActual(@RequestParam user: String): LocalDTO {
        return  localService.getLocalActual(user)
    }

    @PutMapping("/local")
    fun actualizar(@RequestBody body: LocalDTO): LocalDTO {
        return localService.actualizar(body)
    }

    @PostMapping("/local")
    fun crear(@RequestBody body: LocalRequest) =
         localService.crear(body.local, body.adminName)

    @GetMapping("locales")
    fun getAllLocal(): List<LocalDTO> = repository.getAll().map { it.toDTO() }

    @GetMapping("local/{id}")
    fun getLocalDetalle(@PathVariable id: Int): LocalDetalleDTO {
        return localService.getLocalDetalle(id).toDetalleDTO()
    }

    @GetMapping("localCheckout/{localID}/{userID}")
    fun getLocalCheckout(@PathVariable localID: Int, @PathVariable userID: Int): ResponseEntity<LocalPerfilDTO> {
        return ResponseEntity.ok(localService.getLocalCheckout(localID, userID))
    }
    @GetMapping("localesPerfil/{userID}")
    fun getLocalesPerfil(@PathVariable userID: Int): ResponseEntity<List<LocalPerfilDTO>> {
        return ResponseEntity.ok(localService.getLocalesPerfil(userID))
    }
}
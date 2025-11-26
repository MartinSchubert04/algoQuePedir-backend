package algoQuePedir.controller

import algoQuePedir.service.GrupoAlimenticioService
import algoQuePedir.domain.GrupoAlimenticio
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class GrupoAlimenticioController {
    val grupoAlimenticioService: GrupoAlimenticioService = GrupoAlimenticioService()
    @GetMapping("/getGruposAlimenticios")
    fun getAlimenticio() : ResponseEntity<List<GrupoAlimenticio>> {
        return ResponseEntity.ok(grupoAlimenticioService.getAllGrupos())
    }
}
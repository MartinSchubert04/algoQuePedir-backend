package algoQuePedir.controller

import algoQuePedir.errors.NotFoundException
import algoQuePedir.service.PlatoService
import algoQuePedir.domain.repoPlato
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import algoQuePedir.domain.Plato
import algoQuePedir.dto.PlatoDTO
import algoQuePedir.dto.PlatoUpdateDTO
import algoQuePedir.dto.toDTO


@RestController
@CrossOrigin("*")
class PlatoController(val repository: repoPlato, val platoService: PlatoService) {


    @GetMapping("/plato/{id}")
    fun getPlatoById(@PathVariable id: Int, @RequestParam user: String) = platoService.getById(id, user).toDTO()

    @PostMapping("/plato")
    fun crear(@RequestParam user: String, @RequestBody nuevoPlato: PlatoUpdateDTO): Plato {
        return platoService.create(nuevoPlato, user)
    }

    @PutMapping("/plato/{id}")
    fun actualizar(@PathVariable id: Int, @RequestBody body: PlatoUpdateDTO) {
            platoService.update(id, body)
    }

    @GetMapping("/platos")
    fun getAll(@RequestParam user: String): List<PlatoDTO> {
        return  platoService.getAll(user)
    }

    @DeleteMapping("/plato/{id}")
    fun deleteContent(@PathVariable id: Int, @RequestParam user: String ) {
        platoService.deleteById(id, user)
    }
}
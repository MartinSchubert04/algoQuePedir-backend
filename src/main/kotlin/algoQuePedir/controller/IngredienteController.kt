package algoQuePedir.controller
import algoQuePedir.domain.repoIngrediente
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import algoQuePedir.domain.Ingrediente
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping


@RestController
@CrossOrigin("*")
class IngredienteController(val repository: repoIngrediente) {

    @GetMapping("/ingrediente")
    fun getContent(): List<Ingrediente> {
        return  repository.getAll()

    }
    @GetMapping("/ingrediente/{id}")
    fun getContent(@PathVariable id: Int) = repository.getById(id)

    @PutMapping("/ingrediente/{id}")
    fun actualizar(@PathVariable id: Int, @RequestBody body: Ingrediente) {
            repository.update(body)
    }

    @PostMapping("/ingrediente")
    fun getContent(@RequestBody nuevoIngrediente: Ingrediente) {
        // nuevoPlato.validar()
        repository.create(nuevoIngrediente)
    }
    @DeleteMapping("/ingrediente/{id}")
    fun deleteIngrediente(@PathVariable id: Int) {
        repository.deleteById(id)
    }
}
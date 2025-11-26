package algoQuePedir.controller

import algoQuePedir.domain.Usuario
import algoQuePedir.dto.*
import algoQuePedir.service.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class SearchController(val searchService: SearchService) {

    @GetMapping("/search")
    fun bucar(@RequestParam(required = false) id: Int? = null,
              @RequestParam nombreLocal: String): List<LocalSearchDTO> {
        return searchService.search(id, nombreLocal).map { it.toSearchDTO() }
    }
}
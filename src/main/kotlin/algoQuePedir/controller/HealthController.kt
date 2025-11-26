package algoQuePedir.controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin

@RestController
@CrossOrigin("*")
class HealthController {

    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("UP")
    }
}

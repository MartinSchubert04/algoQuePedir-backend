package algoQuePedir.controller


import algoQuePedir.domain.Usuario
import algoQuePedir.domain.repoIngrediente
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoPlato
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.*
import algoQuePedir.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PutMapping


@RestController
@CrossOrigin("*")
class UserController(val repository: repoUsuario, val repositoryIngrediente: repoIngrediente) {

    val userService: UserService = UserService(repository, repoLocal, repositoryIngrediente)

    @GetMapping("/user")
    fun getContent(@RequestParam("username") busquedaUsername: String? = "") = repository.buscar(busquedaUsername)

    @GetMapping("/user/{id}")
    fun getContent(@PathVariable id: Int) = repository.getById(id)

    @PostMapping("/user")
    fun getContent(@RequestBody nuevoUser: Usuario) {
    }


    @GetMapping("/users")
    fun getAllUsers(): List<UserWithPedidosDTO> {
        val users = repository.getAll()

        return users.map { user ->
            UserWithPedidosDTO(
                id = user.id,
                nombre = user.datos.nombre,
                pedidos = user.listaPedidos.map { pedido ->
                    BasicPedidoData(
                        local = pedido.local.nombreLocal,
                        platos = pedido.platos.map { plato -> plato.nombre },
                        metodoPago = pedido.medioDePago
                    )
                }
            )
        }
    }

    @PutMapping("/user/{id}")
    fun updateUsuario(@RequestBody nuevoUsuarioDTO: UserUpdateDTO, @PathVariable id: Int):ResponseEntity<String> {
        try {
            return ResponseEntity.ok(userService.updateUser(id, nuevoUsuarioDTO))
        }catch (e:Exception){
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/confirmarPedido/{localID}/{userID}")
    fun confirmarPedido(@PathVariable localID: Int, @PathVariable userID: Int, @RequestBody pedidoCheckoutDTO: PedidoCheckoutDTO) =
        ResponseEntity.ok(userService.confirmarPedido(localID, userID, pedidoCheckoutDTO))
    @GetMapping("/userPedido/{id}")
    fun getUserPedido(@PathVariable id: Int) = userService.getUserPedidoById(id)

    @GetMapping("/userProfile/{id}")
    fun getUserProfile(@PathVariable id: Int) = userService.getUserProfile(id)

    @PostMapping("/resumenTemporal/{localId}/{userId}")
    fun getResumenTemporal(
        @PathVariable localId: Int,
        @PathVariable userId: Int,
        @RequestBody pedidoFront: PedidoCheckoutDTO
    ): ResumenPedidoDTO {
        return userService.getResumenTemporal(localId, userId, pedidoFront)
    }

}
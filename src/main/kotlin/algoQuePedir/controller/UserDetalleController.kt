package algoQuePedir.controller

import algoQuePedir.dto.toUserDTO
import algoQuePedir.dto.UserPedidoDTO
import algoQuePedir.service.UserDetalleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/pedidos")
@CrossOrigin(origins = ["*"])
class UserPedidoController(
    val userDetalleService: UserDetalleService
) {

    @GetMapping("/{id}/{nombreLocal}")
    fun getPedidoById(
        @PathVariable id: Int?, @RequestParam user: String, @PathVariable nombreLocal: String
    ): UserPedidoDTO? {
        return userDetalleService.getById(id, user, nombreLocal).toUserDTO()
    }

    @GetMapping
    fun getAllPedidos(@RequestParam user: String, @RequestParam(required = false) estado: String?): List<UserPedidoDTO> {
        return userDetalleService.getAll(user, estado).map { it.toUserDTO() }
    }

    @PutMapping("/cancelar")
    fun cancelarPedido(@RequestParam user: String, @RequestBody pedido: UserPedidoDTO): List<UserPedidoDTO> {
        return userDetalleService.cancelarPedido(user, pedido).map { it.toUserDTO() }
    }
}
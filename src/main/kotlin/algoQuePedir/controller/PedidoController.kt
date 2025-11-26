package algoQuePedir.controller

import algoQuePedir.dto.PedidoDTO
import algoQuePedir.errors.BusinessException
import algoQuePedir.errors.NotFoundException
import algoQuePedir.service.PedidoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pedidos")
@CrossOrigin("*")
class PedidoController(
    val pedidoService: PedidoService
) {

    @GetMapping("/{id}")
    fun getPedidoById(
        @PathVariable id: Int?,
        @RequestParam user: String
    ): PedidoDTO {
        // Verifico que exista un local asociado al username
        val locales = pedidoService.repository.buscar(user)
        if (locales.isEmpty()) {
            throw BusinessException("No se encontro un local que pertenezca a $user")
        }

        // Busco el pedido por id
        return pedidoService.getById(id, user)
            ?: throw NotFoundException("Pedido con id $id no encontrado para el local $user")
    }

    @GetMapping
    fun getAllPedidos(
        @RequestParam user: String,
        @RequestParam(required = false) estado: String?
    ): List<PedidoDTO> {
        // Verifico que exista un local asociado al username
        val locales = pedidoService.repository.buscar(user)
        if (locales.isEmpty()) {
            throw BusinessException("No se encontro un local que pertenezca a $user")
        }

        // Devuelvo todos los pedidos del local
        return pedidoService.getAll(user, estado)
    }
}

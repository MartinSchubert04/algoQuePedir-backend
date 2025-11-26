package algoQuePedir.service

import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.dto.toUserDTO
import algoQuePedir.errors.BusinessException
import algoQuePedir.domain.repoUsuario
import algoQuePedir.domain.repoLocal
import algoQuePedir.dto.UserPedidoDTO
import algoQuePedir.domain.Pedido
import org.springframework.stereotype.Service

@Service
class UserDetalleService(val repository: repoUsuario, val repoLocal: repoLocal) {

    fun getAll(usuario: String, estado: String?): List<Pedido> {
        val encontrarUsuario = repository.buscar(usuario)

        if (encontrarUsuario.isEmpty()) {
            throw BusinessException("No se pudo encontrar al usuario $usuario")
        }

        val user = encontrarUsuario.first()
        var pedidos = user.listaPedidos

        if (!estado.isNullOrBlank()) {
            pedidos = pedidos.filter { it.estadoDelPedido.toString() == estado }.toMutableList()
        }

        return pedidos
    }

    fun getById(id: Int?, usuario: String, nombreLocal: String): Pedido {
        if (id == null) {
            throw BusinessException("ID de Pedido no encontrado")
        }

        val encontrarUsuario = repository.buscar(usuario)

        if (encontrarUsuario.isEmpty()) {
            throw BusinessException("No se pudo encontrar al usuario $usuario")
        }

        val listaDePedidosUsuario = encontrarUsuario.first().listaPedidos

        val pedidoPorID = listaDePedidosUsuario.find { it.id == id && it.local.nombreLocal ==  nombreLocal}

        if (pedidoPorID == null) {
            throw BusinessException("Pedido con ID: $id no encontrado para el usuario $usuario")
        }

        return pedidoPorID
    }

    fun cancelarPedido(usuario: String, pedido: UserPedidoDTO): List<Pedido> {
        val encontrarUsuario = repository.buscar(usuario)
        val encontrarLocal = repoLocal.buscar(pedido.nombreLocal)

        if (encontrarUsuario.isEmpty()) {
            throw BusinessException("No se pudo encontrar al usuario $usuario")
        }

        if (encontrarLocal.isEmpty()) {
            throw BusinessException("No se pudo encontrar el local para cancelar el pedido")
        }

        val user = encontrarUsuario.first()
        val pedidoExistente = user.listaPedidos.find { it.id == pedido.id && it.local.nombreLocal == pedido.nombreLocal}
            ?: throw BusinessException("Pedido con ID: ${pedido.id} no encontrado para el usuario $usuario")

        val local = encontrarLocal.first()
        val pedidoExistenteLocal = local.listaPedidos.find { it.id == pedido.id }
            ?: throw BusinessException("Pedido con ID: ${pedido.id} no encontrado para el local $local")

        if (pedidoExistente.estadoDelPedido == EstadoDelPedido.PENDIENTE) {
            pedidoExistente.estadoDelPedido = EstadoDelPedido.CANCELADO
            pedidoExistenteLocal.estadoDelPedido = EstadoDelPedido.CANCELADO
        } else {
            throw BusinessException("No se puede cancelar un pedido ya entregado o cancelado")
        }

        repository.updateById(user.id!!, user)
        repoLocal.updateById(local.id!!, local)

        return user.listaPedidos
    }
}

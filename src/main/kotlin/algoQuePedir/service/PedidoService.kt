package algoQuePedir.service

import algoQuePedir.dto.PedidoDTO
import algoQuePedir.dto.toDTO
import algoQuePedir.errors.BusinessException
import algoQuePedir.domain.repoLocal
import org.springframework.stereotype.Service

@Service
class PedidoService(val repository: repoLocal) {

    fun getAll(adminName: String, estado: String? ): List<PedidoDTO> {

        val localesHallados = repository.buscar(adminName)

        if (localesHallados.isEmpty()) return emptyList()

        val local = localesHallados.first()

        var pedidos = local.listaPedidos

        if (!estado.isNullOrBlank()) {
            pedidos = pedidos.filter { it.estadoDelPedido.toString() == estado }.toMutableList()
        }

        return pedidos.map { it.toDTO() }
    }

    fun getById(id: Int?, adminName: String): PedidoDTO? {
        val localesHallados = repository.buscar(adminName)

        val listaDelLocal = localesHallados.single().listaPedidos

        if (id == null) {
            throw BusinessException("Debe proveerse el ID del local a actualizar")
        }

        val pedidoPorID = listaDelLocal.find { it.id == id }

        if (pedidoPorID == null) {
            return null
        }

        return pedidoPorID.toDTO()
    }
}
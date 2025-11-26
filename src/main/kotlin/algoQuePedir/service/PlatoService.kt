package algoQuePedir.service

import algoQuePedir.errors.BusinessException
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoPlato
import org.springframework.stereotype.Service
import algoQuePedir.domain.Plato
import algoQuePedir.dto.PlatoDTO
import algoQuePedir.dto.PlatoUpdateDTO
import algoQuePedir.dto.toDTO
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Service
class PlatoService(val repository: repoLocal, val platoRepository: repoPlato) {

    fun getAll(adminName: String ): List<PlatoDTO> {
        val localesHallados = repository.buscar(adminName)

        if (localesHallados.isEmpty()) return emptyList()

        val local = localesHallados.first()

        return local.menu.map { it.toDTO() }
    }

    fun deleteById(id: Int, adminName: String) {
        val localesHallados = repository.buscar(adminName)
        val local = localesHallados.first()

        val eliminado = local.menu.removeIf { it.id == id }
        if (!eliminado) throw BusinessException("No existe el plato en el local")

        val platoEnRepo = platoRepository.getById(id)
        if (platoEnRepo != null) {
            platoRepository.delete(platoEnRepo)
        }
    }

    fun getById(id: Int?, adminName: String): Plato {
        if (id == null) {
            throw BusinessException("Debe proveerse el ID del plato a buscar")
        }
        val localesHallados = repository.buscar(adminName)
        if (localesHallados.isEmpty()) throw BusinessException("Local no encontrado")
        val local = localesHallados.first()

        val platoHallado = local.menu.find { it.id == id }
            ?: throw BusinessException("No se encontro el plato con id $id en tu local")

        return platoHallado
    }

    fun create(dto: PlatoUpdateDTO, adminName: String): Plato {
        val localesHallados = repository.buscar(adminName)

        if (localesHallados.isEmpty()) throw BusinessException("Debe crear un local para poder crear un plato")

        val local = localesHallados.first()

        val nuevoPlato = Plato(
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            ingredientes = dto.ingredientes,
            local = local,
            descuento = dto.descuento,
            fechaLanzamiento = dto.fechaLanzamiento ?: LocalDateTime.now(),
            esDeAutor = dto.esDeAutor,
            porcentajeRegalia = dto.porcentajeRegalia,
            valorBase = dto.valorBase
        )
        nuevoPlato.imagen = dto.imagen
        nuevoPlato.id = null
        platoRepository.create(nuevoPlato)

        local.agregarPlato(nuevoPlato)


        return nuevoPlato
    }

    fun update(id: Int, dto: PlatoUpdateDTO) {
        val platoExistente = platoRepository.getById(id)
            ?: throw BusinessException("No se encontró el plato con id $id")

        platoExistente.nombre = dto.nombre
        platoExistente.descripcion = dto.descripcion
        platoExistente.imagen = dto.imagen
        platoExistente.valorBase = dto.valorBase
        platoExistente.esDeAutor = dto.esDeAutor
        platoExistente.ingredientes = dto.ingredientes
        platoExistente.descuento = dto.descuento
        platoExistente.porcentajeRegalia = dto.porcentajeRegalia

    }
}
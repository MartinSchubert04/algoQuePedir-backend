package algoQuePedir.service

import algoQuePedir.domain.Usuario
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.LocalDTO
import algoQuePedir.dto.toDTO
import algoQuePedir.errors.BusinessException
import algoQuePedir.errors.NotFoundException
import local.Local
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: repoLocal, val userRepository: repoUsuario) {

    fun search(id: Int? = null, nombreLocal: String): List<Local> {
        if( id != null ) return buscarCercanos(id, nombreLocal)
        return repository.buscar(nombreLocal)
    }

    fun buscarCercanos(id: Int, nombreLocal: String): List<Local> {
        val ubicacionUser = userRepository.getById(id)?.ubicacion?.coordenadas
            ?: throw BusinessException("No se encontro usuario con ese id")

        val localesByQuery = repository.buscar(nombreLocal)
        val listaLocalesCercanos = localesByQuery.sortedBy { ubicacionUser.distance(it.direccion.coordenadas) }

        return listaLocalesCercanos
    }
}
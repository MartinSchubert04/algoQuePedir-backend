package algoQuePedir.service

import algoQuePedir.domain.Usuario
import algoQuePedir.errors.BusinessException
import algoQuePedir.errors.NotFoundException
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.*
import local.Local
import org.springframework.stereotype.Service


@Service
class LocalService(val repository: repoLocal, val repoUser: repoUsuario) {

    fun getLocalActual(adminName: String): LocalDTO {
        val locales = repository.buscar(adminName)
        if (locales.isEmpty()) {
            return LocalDTO(
                nombreLocal = "",
                imgURL = "",
                calle = "",
                longitud = 0.0,
                latitud = 0.0,
                altura = 0,
                porcentajeComision = 0.0,
                porcentajeAutor = 0.0,
            )
        }

        return locales.first().toDTO()
    }

    fun getLocalDetalle(id: Int): Local {
        return repository.getById(id) ?: throw NotFoundException("No se encontro local con id: $id")
    }


    fun actualizar (localActualizado: LocalDTO): LocalDTO {
        val localNuevo = localActualizado.toEntity()

        if (localNuevo.id == null) {
            throw BusinessException("Debe proveerse el ID del local a actualizar")
        }
        localNuevo.validar()

        val localExistente = localPorId(localNuevo.id!!)

        localExistente.actualizar(localNuevo)
        repository.update(localExistente)

        return localExistente.toDTO()
    }

    fun crear(nuevoLocal: LocalDTO, adminName: String): LocalDTO {
        val local = nuevoLocal.toEntity()
        local.validar()
        asignar(local, adminName)

        repository.create(local)

        return local.toDTO()
    }

    fun localPorId(id: Int): Local =
        repository.getById(id) ?: throw NotFoundException("No se encontró el local de id <$id>")

    fun asignar(nuevoLocal: Local, adminName: String) {
        nuevoLocal.adminUsername = adminName
    }



    fun aceptarPuntaje(localId: Int, puntaje: Int) {
        val local = repository.getById(localId)
            ?: throw BusinessException("No se encontró un local con ID $localId")
        if (puntaje !in 1..5) {
            throw BusinessException("El puntaje debe estar entre 1 y 5")
        }
        local.aceptarPuntaje(puntaje);
    }

    fun getLocalCheckout(id: Int, userId: Int): LocalPerfilDTO{
        val currUser: Usuario = repoUser.getById(userId) ?: throw NotFoundException("No se encontro al usuario")
        val local =  repository.getById(id) ?: throw NotFoundException("No se encontro local con ID $id")
        return LocalPerfilDTO(
            nombreLocal = local.nombreLocal,
            imgPath = local.imgURL,
            distancia = ((local.direccion.coordenadas.distance(currUser.ubicacion.coordenadas))/1000).redondearADosDecimales(),
            promedio = local.promedioPuntajes(),
            tipoDelivery = "Gratis",
            mediosDePago = local.mediosDePago
        )
    }

    fun getLocalesPerfil(userId: Int): MutableList<LocalPerfilDTO>{
        val currUser: Usuario = repoUser.getById(userId) ?: throw NotFoundException("No se encontro al usuario")
        val locales: List<Local> = repository.getAll()
        val response: MutableList<LocalPerfilDTO> = mutableListOf()
        locales.forEach { local ->
            response.add(LocalPerfilDTO(
                nombreLocal = local.nombreLocal,
                imgPath = local.imgURL,
                distancia = local.direccion.coordenadas.distance(currUser.ubicacion.coordenadas),
                promedio = local.promedioPuntajes(),
                tipoDelivery = "Gratis",
                mediosDePago = local.mediosDePago
            ))
        }
        return response
    }
}
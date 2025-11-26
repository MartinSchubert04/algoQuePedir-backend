package algoQuePedir.service

import algoQuePedir.domain.GrupoAlimenticio

class GrupoAlimenticioService {
    fun getAllGrupos(): List<GrupoAlimenticio> {
        return GrupoAlimenticio.entries.toList()
    }
}
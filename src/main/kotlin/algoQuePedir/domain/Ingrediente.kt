package algoQuePedir.domain

import algoQuePedir.domain.GrupoAlimenticio
import algoQuePedir.domain.implementaId


class Ingrediente constructor (
    val nombre: String,
    val costoMercado: Double,
    grupoAlimenticio: GrupoAlimenticio,
    val esDeOrigenAnimal: Boolean
): implementaId {
    override var id: Int? = null

    var grupoAlimenticio: GrupoAlimenticio = grupoAlimenticio
        get() = field  // Getter que devuelve el valor almacenado
        set(value) {
            field = value  // Asigna un nuevo valor del enum
        };
}
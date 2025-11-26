package algoQuePedir.domain
import local.Local
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Plato (
    var nombre: String,
    var descripcion: String,
    var ingredientes: MutableList<Ingrediente>,
    val local: Local,
    var descuento: Double = 0.0,
    var fechaLanzamiento: LocalDateTime,
    var esDeAutor: Boolean = false,
    var porcentajeRegalia: Double = 0.0,
    var valorBase: Double
    ): implementaId {

    override var id: Int? = null
    var cantidad: Int?= null

    lateinit var imagen: String


    val costoProduccion:Double
        get() = ingredientes.sumOf{ it.costoMercado.toDouble() }

    fun esPromo() = descuento > 0.0

    fun esNuevo(): Boolean {
        val fechaActual = LocalDateTime.now()
        val diasDesdeLanzamiento = ChronoUnit.DAYS.between(fechaLanzamiento, fechaActual)
        return diasDesdeLanzamiento <= 30
    }

    private fun descuentoPlatoNuevo(): Double {
        val fechaActual = LocalDateTime.now()
        val diasDesdeLanzamiento = ChronoUnit.DAYS.between(fechaLanzamiento, fechaActual)


        return if(diasDesdeLanzamiento <= 20) 0.30 - (diasDesdeLanzamiento * 0.01)
        else if(diasDesdeLanzamiento in 21..30) 0.10
        else 0.0
    }

    fun valorBaseAplicado() = valorBase + costoProduccion + (local.porcentajeBeneficio * valorBase)

    fun agregarRegalia() = valorBase * porcentajeRegalia

    fun precioSinDescuentos() = valorBaseAplicado() + if(esDeAutor) agregarRegalia() else 0.0

    fun precioFinal(): Double {
        var precio = precioSinDescuentos()

        // aplico descuento de nuevo, si no lo es entonces aplico el que tenga o ninguno en caso de 0
        if (esNuevo()) {
            precio *= 1 - descuentoPlatoNuevo()
        } else {
            precio *= 1 - descuento
        }

        return precio
    }

    fun update(plato: Plato) {
        this.imagen = plato.imagen
    }
}

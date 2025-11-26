package algoQuePedir.domain

import local.Local

// CONDICIONES DE ACEPTACION CON STRATEGY
interface CriterioAceptacion {
    fun cumple(pedido: Pedido): Boolean
}

object SencilloDel : CriterioAceptacion {
    override fun cumple(pedido: Pedido): Boolean = true
}
class CriterioHorarioSeguro (val horarioSeguro: IntRange): CriterioAceptacion {
    override fun cumple(pedido: Pedido) =  pedido.momentoDeOrden.hour in horarioSeguro
}

class CriterioMontoMinimo (val montoMinimo: Double): CriterioAceptacion {
    override fun cumple(pedido: Pedido) =  pedido.totalAPagar() >= montoMinimo
}

class CriterioSoloLocalesAmigos (val localesAmigos: MutableList<Local>) : CriterioAceptacion {
    override fun cumple(pedido: Pedido) =  pedido.local in this.localesAmigos
}

class CriterioCertificado : CriterioAceptacion {
    override fun cumple(pedido: Pedido) =  pedido.esCertificado()
}
// Entregra 2, criterios multiples

class CriterioY(private val criterios: List<CriterioAceptacion>) : CriterioAceptacion {
    override fun cumple(pedido: Pedido) = criterios.all { it.cumple(pedido) }
}

class CriterioO(private val criterios: List<CriterioAceptacion>) : CriterioAceptacion {
    override fun cumple(pedido: Pedido) = criterios.any { it.cumple(pedido) }
}
package algoQuePedir.domain

import local.Local


interface Auditores {
    fun auditar(unLocal: Local) : Boolean
    fun descripcion(): String

}

class auditorVentasAcumuladas (private val montoObjetivo: Double): Auditores {
    override fun auditar(unLocal: Local) = unLocal.listaPedidos.sumOf { it.totalAPagar() } >= montoObjetivo
    /*override fun descripcion() = "Ventas mayores al monto seteado"*/
    override fun descripcion() = throw RuntimeException("Ventas mayores al monto seteado")

}

class auditorPlatosPorPedido (): Auditores {
    override fun auditar(unLocal: Local): Boolean{
        val tresOMasPlatos = unLocal.listaPedidos.count { it.platos.size >= 3 }
        return tresOMasPlatos >= 5

    }
    override fun descripcion() = throw RuntimeException("Se vendieron mas de 5 veces 3 o más platos")

    /*override fun descripcion() = "Se vendieron mas de 5 veces 3 o más platos"*/
}

class auditorPLatosVeganos (val metaVeganos: Int): Auditores {
    override fun auditar(unLocal: Local): Boolean{
        val cantidadTotal  = unLocal.listaPedidos.sumOf {cantidadVeganos(it)}
        return cantidadTotal >= metaVeganos
    }
    /*override fun descripcion() = "Se alcanzo la meta de platos veganos"*/
    override fun descripcion() = throw RuntimeException("Se alcanzo la meta de platos veganos")
    fun esVegano(unPlato: Plato) = unPlato.ingredientes.all { !it.esDeOrigenAnimal }
    fun cantidadVeganos(unPedido: Pedido) = unPedido.platos.count { esVegano(it) }

}

class auditorCombinado(private val auditorias: List<Auditores>) : Auditores {
    override fun auditar(unLocal: Local) = auditorias.all { it.auditar(unLocal) }

    override fun descripcion() =  "Todas las auditorias fueron exitosas"

}


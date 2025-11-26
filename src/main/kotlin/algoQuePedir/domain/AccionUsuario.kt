package algoQuePedir.domain

import local.Local
import kotlin.random.Random

interface AccionUsuario {
    fun ejecutar(usuario: Usuario)

}


class AccionEstablecerPedido(
    protected val pedido: Pedido

) : AccionUsuario {
    override fun ejecutar(usuario: Usuario) {
        usuario.confirmarPedido(pedido)
    }
}


// Clase base abstracta que define el algoritmo común para puntuar un local
abstract class AccionPuntuarTemplate(
    protected val local: Local
) : AccionUsuario {

    // Este es el template que define la estructura del algoritmo
    override fun ejecutar(usuario: Usuario) {
        val puntaje = calcularPuntaje(usuario) // Paso variable
        usuario.puntuarLocal(local, puntaje)   // Paso común (la primitiva)
    }

    //  abstracto: cada subclase debe definir cómo se calcula el puntaje
    protected abstract fun calcularPuntaje(usuario: Usuario): Int
}

class AccionPuntuarAleatorio(
    local: Local,
    private val min: Int = 1,
    private val max: Int = 10
) : AccionPuntuarTemplate(local) {

    override fun calcularPuntaje(usuario: Usuario): Int {
        return Random.nextInt(min, max)
    }
}

class AccionPuntuarConPromedio(
    local: Local
) : AccionPuntuarTemplate(local) {

    override fun calcularPuntaje(usuario: Usuario): Int {
        return local.puntajes.average().toInt()
    }
}

class AccionPuntuarFijo(
    local: Local,
    private val puntajeFijo: Int
) : AccionPuntuarTemplate(local) {
    override fun calcularPuntaje(usuario: Usuario): Int {

        return puntajeFijo

    }
}
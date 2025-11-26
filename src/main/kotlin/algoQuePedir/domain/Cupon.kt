package cupon
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.implementaId
import local.Local
import java.time.LocalDateTime

abstract class Cupon(
    val fechaEmision : LocalDateTime,
    val duracion : Long,
    val porcentajeBase: Double

): implementaId {
    override var id: Int? = null
    var aplicado : Boolean = false
    fun vencido() = LocalDateTime.now() > fechaEmision.plusDays(duracion)
    fun puedoAplicar(unPedido: Pedido): Boolean = cuponActivo(unPedido) && !aplicado && descuentoMenorPedido(unPedido) && condicionEspecial(unPedido)
    fun descuentoBase(unPedido: Pedido): Double = calcularDescuento(unPedido.sumaPlatos(), porcentajeBase/100)
    fun cuponActivo(unPedido: Pedido): Boolean = unPedido.momentoDeOrden in fechaEmision..(fechaEmision.plusDays(duracion))
    fun descuentoMenorPedido(unPedido: Pedido): Boolean = devuelvoDescuento(unPedido) <= unPedido.sumaPlatos()
    fun calcularDescuento(unMonto : Double, unPorcentaje : Double) = unMonto * unPorcentaje
    fun devuelvoDescuento(unPedido: Pedido):Double = descuentoBase(unPedido) + descuentoEspecial(unPedido)
    fun descuentoTotal(unPedido: Pedido) :Double{
        if(puedoAplicar(unPedido)){
            aplicado = true
            return devuelvoDescuento(unPedido)
        }else{
            return 0.0
        }
    }
    abstract fun descuentoEspecial(unPedido: Pedido): Double
    abstract fun condicionEspecial(unPedido: Pedido): Boolean

}

class cuponDescuentoSegunDia(
    fechaEmision : LocalDateTime,
    duracion : Long,
    porcentajeBase: Double,
    val diaSemana: Int) : Cupon(fechaEmision, duracion, porcentajeBase) {

    override fun descuentoEspecial(unPedido: Pedido) : Double{
        return if(unPedido.platos.any{ esMismoDia(it.fechaLanzamiento)}){
            unPedido.sumaPlatos() * 10/100
        }else{
            unPedido.sumaPlatos() * 5/100
        }
    }

    fun esMismoDia(unaFecha: LocalDateTime): Boolean = unaFecha.dayOfWeek.value== diaSemana
    override fun condicionEspecial(unPedido: Pedido): Boolean = esMismoDia(unPedido.momentoDeOrden)

}

class cuponDescuentoSegunLocal(
     fechaEmision : LocalDateTime,
     duracion : Long,
     porcentajeBase: Double,
    val localesDescuento: List<Local>) : Cupon(fechaEmision, duracion, porcentajeBase) {

    override fun descuentoEspecial(unPedido: Pedido): Double{
        return if(unPedido.esCertificado()){
            1000.0
        }else{
            500.0
        }
    }

    override fun condicionEspecial(unPedido: Pedido) : Boolean = localesDescuento.contains(unPedido.local)

}

class cuponDescuentoSegunTope(
    fechaEmision : LocalDateTime,
    duracion : Long,
    porcentajeBase: Double,
    val tope: Double,
    val porcentajeAdicional: Double) : Cupon(fechaEmision, duracion, porcentajeBase) {

    override fun descuentoEspecial(unPedido: Pedido): Double {
        val descuentoEspecial: Double = calcularDescuento(unPedido.sumaPlatos(),porcentajeAdicional/100)
        return if(descuentoEspecial <= tope){
            descuentoEspecial
        }else{
            tope
        }
    }

    override fun condicionEspecial(unPedido: Pedido) : Boolean = true;

}



package algoQuePedir.domain
import algoQuePedir.domain.CriterioAceptacion
import org.uqbar.geodds.Polygon
import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.SencilloDel
import algoQuePedir.domain.DatosPersonales
import algoQuePedir.domain.implementaId

//import local.Local
//import pedido.domain.Pedido

class Delivery(
    val datos: DatosPersonales,
    //Tienen nombre, un username, y password. Estos, para aceptar entregar un pedido

    val zonaTrabajo: Polygon,
    var criterioAceptacion: CriterioAceptacion = SencilloDel
    //de esta forma chekeo todas las condiciones
): implementaId {
    override var id: Int? = null
    fun puedeAceptarPedido(pedido: Pedido): Boolean =
        pedido.estadoDelPedido == EstadoDelPedido.PREPARADO &&
        criterioAceptacion.cumple(pedido) &&
        dentroDeZona(pedido)


    fun dentroDeZona(pedido: Pedido) : Boolean{
        return (zonaTrabajo.isInside(pedido.local.direccion.coordenadas) &&
                zonaTrabajo.isInside(pedido.cliente.ubicacion.coordenadas))
    }
}



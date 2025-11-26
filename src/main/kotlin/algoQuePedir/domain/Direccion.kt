package algoQuePedir.domain
import org.uqbar.geodds.Point

class Direccion(val calle:String, val coordenadaX: Double, val coordenadaY: Double, val altura:Int) {

    val coordenadas = Point(coordenadaX, coordenadaY)
}
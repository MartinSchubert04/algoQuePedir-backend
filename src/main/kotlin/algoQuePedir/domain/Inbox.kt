package algoQuePedir.domain

class Inbox {

    var mensajes: MutableList<Mensaje> = mutableListOf<Mensaje>()
    fun recibirMensaje(unMensaje: Mensaje){
        mensajes.add(unMensaje)
    }
    fun marcarLeido(posicion: Int){
        mensajes[posicion].leido = true
    }
}
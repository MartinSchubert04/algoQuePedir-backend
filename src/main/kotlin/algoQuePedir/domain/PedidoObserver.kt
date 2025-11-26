package algoQuePedir.domain

import java.time.LocalDateTime


interface PedidoObserver {
    fun pedidoRealizado(pedido: Pedido)
}

class BasicObserver : PedidoObserver {
    override fun pedidoRealizado(pedido: Pedido) {}
}

class MailPublicitarioObserver(private val mailSender: MailSender) : PedidoObserver {
    override fun pedidoRealizado(pedido: Pedido) {
        val mail = Mail(
            from = pedido.cliente.datos.nombre,
            to = pedido.local.nombreLocal,
            subject = "Más recomendaciones de ${pedido.local.nombreLocal} ",
            content = "Te recomendamos los siguientes platos del mismo:"
        )
        pedido.local.menu.forEach {
            if (pedido.cliente.aceptaPlato(it)) {
                mailSender.sendMail(mail)
            }
        }
    }
}

class CambiarAVeganoObserver : PedidoObserver {
    override fun pedidoRealizado(pedido: Pedido) {
        if(pedido.platos.all { !it.ingredientes.any { it.esDeOrigenAnimal } }){
            if(!pedido.cliente.tieneCondicion(Veganos)){
                pedido.cliente.cambiarCondicion( CondicionY(mutableListOf(pedido.cliente.condicion, Veganos)) )
            }
        }
    }
}

class PedidoCertificadoObserver : PedidoObserver {
    override fun pedidoRealizado(pedido: Pedido) {
        val unMensaje = Mensaje(LocalDateTime.now(), "domain.Pedido Certificado", "Priorizar el pedido", false)
        if(pedido.esCertificado()){
            pedido.local.inbox.recibirMensaje(unMensaje)
        }
    }
}



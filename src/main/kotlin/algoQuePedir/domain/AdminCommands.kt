package algoQuePedir.domain

import local.Local
import java.time.LocalDateTime


abstract class Instruccion(val mailSender: MailSender) {
    protected abstract val nombreProceso: String
    protected abstract fun ejecutarProceso()

    fun ejecutar() {
        ejecutarProceso()
        enviarMail()
    }
    private fun enviarMail() {
        mailSender.sendMail(
            Mail(
                from = "Admin",
                to = "admin@aqp.com.ar",
                subject = "Proceso ejecutado: $nombreProceso",
                content = "Se realizó el proceso: $nombreProceso"
            )
        )
    }
}


class BorrarMensajes(mailSender: MailSender): Instruccion(mailSender) {
    override val nombreProceso = "Borrar Mensajes Antiguos y Leídos"
    override fun ejecutarProceso() {
        repoLocal.coleccion.forEach { local ->
        local.inbox.mensajes.removeAll { it.fecha < LocalDateTime.now().minusDays(30) && it.leido }}
    }
}

class ActualizarIngredientes(mailSender: MailSender, val servicioIngredientes: ServicioIngredientes): Instruccion(mailSender) {
    override val nombreProceso = "Actualizar Ingredientes"
    override fun ejecutarProceso() {
        repoIngrediente.getFromService(servicioIngredientes)
    }
}

class BorrarCupones(mailSender: MailSender) : Instruccion(mailSender) {
    override val nombreProceso = "Borrar cupones vencidos sin aplicar"
    override fun ejecutarProceso() {
        repoCupon.coleccion.removeAll { !it.aplicado && it.vencido() }
    }
}

class AgregarLocales(mailSender: MailSender, val lista: List<Local>): Instruccion(mailSender) {
    override val nombreProceso = "Agregar locales de forma masiva"
       override fun ejecutarProceso() {
        this.lista.forEach { local -> repoLocal.coleccion.add(local) }
    }
}


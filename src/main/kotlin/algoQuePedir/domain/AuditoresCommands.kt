package algoQuePedir.domain

import local.Local

class EjecutarAuditoria(val auditor: Auditores) {

    val localesSuscriptos = mutableListOf<Local>()
    val resultadoAudicion = mutableMapOf<Local, Boolean>()

    fun ejecutar(unLocal: Local): String? {
        return if (auditor.auditar(unLocal)) {
            auditor.descripcion()
        } else {
            null
        }
    }

    fun ejecutarMasivo(){
        localesSuscriptos.forEach {
            if(auditor.auditar(it)){
                resultadoAudicion.put(it, true)
            }
            else{
                resultadoAudicion.put(it, false)
            }
        }
    }

    fun suscribirLocal(local:Local){ localesSuscriptos.add(local) }
    fun quitarSuscripcionLocal(local: Local){ localesSuscriptos.remove(local) }
}



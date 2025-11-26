package algoQuePedir.domain

import algoQuePedir.errors.BusinessException
import local.Local
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Usuario constructor(
    val datos: DatosPersonales,
    val fechaNacimiento: String,
    val ubicacion: Direccion,
    var condicion: CondicionPlatoUsuario = Sencillo,
    val fechaDeCreacion: LocalDate = LocalDate.now(),
    var observers: MutableList<PedidoObserver> = mutableListOf(BasicObserver())
) : implementaId {

    override var id: Int? = null
    var localesPreferidos: MutableList<Local> = mutableListOf()
    var listaPedidos: MutableList<Pedido> = mutableListOf()
    var ingredientesPreferidos: MutableList<Ingrediente> = mutableListOf()
    var ingredientesAEvitar: MutableList<Ingrediente> = mutableListOf()
    var palabrasMarketing = mutableListOf("nutritivo", "bajo en sodio", "sin azúcar")
    var stubEdad: Int? = null
    var distanciaMax: Double = 5.0

        val imagen: String
            get() = when (datos.username.lowercase()) {
                    "smiller2005" -> "/src/lib/assets/fotom.jpg"
                else -> "/src/lib/assets/default-user.svg"
            }
    //entrega 3
    private val accionesPendientes = mutableListOf<AccionUsuario>()

    fun edad(fechaNacimiento: String = this.fechaNacimiento): Int {
        stubEdad?.let { return it }

        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        return Period.between(
            LocalDate.parse(fechaNacimiento, dateFormatter),
            LocalDate.now()
        ).years
    }

    fun stubEdad(edad: Int) {
        stubEdad = edad
    }

    fun localCercano(local: Local) = this.ubicacion.coordenadas.distance(local.direccion.coordenadas) <= distanciaMax

    fun confirmarPedido(pedido: Pedido) {
        listaPedidos.add(pedido)
        // Avisamos a los observers que se confirmó el pedido
        observers.forEach { it.pedidoRealizado(pedido) }
        pedido.local.agregarPedido(pedido)
    }

    fun fechaUltimoPedido(local: Local): LocalDateTime {
        return listaPedidos
            .filter { it.local === local }
            .maxOf { it.momentoDeOrden }
    }
    // agrego condicion de pendiente en entrega 3
    fun sePuedePuntuar(local: Local) = ChronoUnit.DAYS.between(fechaUltimoPedido(local), LocalDateTime.now()) <= 7 && !local.pendiente

    fun puntuarLocal(local: Local, puntaje: Int) {
        if (sePuedePuntuar(local)) {
            local.aceptarPuntaje(puntaje)
            local.pendiente=true
        }
        else{throw RuntimeException("Este local ya fue puntuado")}
    }

    fun tiempoRegistrado(): Long = ChronoUnit.YEARS.between(fechaDeCreacion, LocalDate.now())

    fun aceptaPlato(plato: Plato): Boolean {
        return (
                filtrarPorIngrediente(plato = plato) &&
                        condicion.aceptaPlato(plato = plato, usuario = this)
                )
    }

    fun filtrarPorIngrediente(plato: Plato): Boolean = !ingredientesAEvitar.any { plato.ingredientes.contains(it) }

    fun agregarEvitar(nuevoIngrediente: Ingrediente) {
        if (!this.ingredientesPreferidos.contains(nuevoIngrediente)) {
            this.ingredientesAEvitar.add(nuevoIngrediente)
        } else if (this.ingredientesAEvitar.contains(nuevoIngrediente)) {
            throw RuntimeException("El ingrediente ya se encuentra en la lista de evitar")
        } else {
            throw RuntimeException("El ingrediente no puede ser evitado si es preferido")
        }
    }

    // FUNCIONES PARA MODIFICAR ATRIBUTOS DEL USUARIO
    fun agregarPreferido(nuevoIngrediente: Ingrediente) {
        if (!this.ingredientesAEvitar.contains(nuevoIngrediente)) {
            this.ingredientesPreferidos.add(nuevoIngrediente)
        } else if (this.ingredientesPreferidos.contains(nuevoIngrediente)) {
            throw RuntimeException("El ingrediente ya se encuentra en la lista de preferidos")
        } else {
            throw RuntimeException("El ingrediente no puede ser preferido si debe ser evitado")
        }
    }

    fun agregarLocalPreferido(local: Local) {
        if (this.localesPreferidos.contains(local)) {
            throw BusinessException("El local ya está en la lista de preferidos")
        } else if (this.localesPreferidos.contains(local)) {
            throw BusinessException("El local ya se encuentra en la lista de preferidos")
        } else {
            this.localesPreferidos.add(local)
        }
    }

    fun limpiarIngredientesPreferidos() {
        this.ingredientesPreferidos = mutableListOf()
    }

    fun limpiarIngredientesEvitar() {
        this.ingredientesAEvitar = mutableListOf()
    }

    fun limpiarLocalesPreferidos() {
        this.localesPreferidos = mutableListOf()
    }

    fun cambiarCondicion(nuevaCondicion: CondicionPlatoUsuario) {
        this.condicion = nuevaCondicion
    }
    fun limpiarPalabrasMarketing(){
        this.palabrasMarketing = mutableListOf()
    }
        //entrega 3

    fun ejecutarAcciones() {
        accionesPendientes.forEach { it.ejecutar(this) }
        accionesPendientes.clear()
    }


    fun tieneCondicion(condicionBuscada: CondicionPlatoUsuario): Boolean{
        return when(val cond = this.condicion){
            condicionBuscada -> true
            is CondicionY -> cond.condiciones.any { it == condicionBuscada }
            else -> false
        }
    }

    /* OBSERVERS FUNCTIONS */
    fun subscribeObserver(newObserver: PedidoObserver) {
        observers.add(newObserver)
    }
    fun deleteObserver(observer: PedidoObserver) {
        observers.remove(observer)
    }
    fun clearObservers() {
        observers.clear()
    }
    fun clearCondicion(){
        condicion = Sencillo
    }

    fun validate(username: String, password: String) = username == this.datos.username && password == this.datos.password

    fun validarRegistro(passConfirmation: String) {
        when {
            this.datos.username.isBlank() || this.datos.password.isBlank() || passConfirmation.isBlank() -> throw BusinessException("No puede haber campos vacios")
            this.datos.password != passConfirmation -> throw BusinessException("Las contraseñas no coinciden")
        }
    }
}
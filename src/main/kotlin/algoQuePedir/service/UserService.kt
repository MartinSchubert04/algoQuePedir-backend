package algoQuePedir.service

import algoQuePedir.domain.BasicObserver
import algoQuePedir.domain.DatosPersonales
import algoQuePedir.domain.Direccion
import algoQuePedir.domain.EstadoDelPedido
import algoQuePedir.domain.Ingrediente
import algoQuePedir.domain.Pedido
import algoQuePedir.domain.PedidoObserver
import algoQuePedir.domain.Plato
import algoQuePedir.domain.Sencillo
import algoQuePedir.domain.Usuario
import algoQuePedir.domain.repoIngrediente
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoUsuario
import algoQuePedir.dto.*
import algoQuePedir.errors.BusinessException
import algoQuePedir.errors.NotFoundException
import jakarta.websocket.RemoteEndpoint
import local.Local
import org.apache.catalina.User
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.springframework.stereotype.Service
import kotlin.math.roundToLong
fun Double.redondearADosDecimales(): Double {
    return if (this.isNaN() || this.isInfinite()) {
        this } else {(this * 100).roundToLong() / 100.0}}

@Service
class UserService(
    private val repository: repoUsuario,
    private val repositoryLocal: repoLocal,
    private val repoIngrediente: repoIngrediente,
) {

    fun updateUser(id: Int, nuevoUsuarioDTO: UserUpdateDTO): String {
        val viejoUsuario = repository.getById(id)
            ?: throw RuntimeException("Usuario con id $id inexistente")

        val basicObserver: PedidoObserver = BasicObserver()
        val nuevoUsuario = Usuario(
            datos = DatosPersonales(
                nuevoUsuarioDTO.nombre,
                nuevoUsuarioDTO.apellido,
                // Estos campos no se actualizan en esta funcion
                viejoUsuario.datos.username,
                viejoUsuario.datos.password
                ),
            fechaNacimiento = viejoUsuario.fechaNacimiento, // Tampoco se actualiza aca
            ubicacion = Direccion(
                nuevoUsuarioDTO.direccion,
                nuevoUsuarioDTO.latitud,
                nuevoUsuarioDTO.longitud,
                altura = nuevoUsuarioDTO.altura
            ),
            observers = mutableListOf(basicObserver), // No se actualiza aca
        ).apply {
            cambiarCondicion(nuevoUsuarioDTO.condicion)
            nuevoUsuarioDTO.ingredientesPreferidos.forEach { ing ->
                agregarPreferido(repoIngrediente.buscar(ing).firstOrNull()
                    ?: throw RuntimeException("Ingrediente '$ing' no encontrado!"))
            }
            nuevoUsuarioDTO.ingredientesEvitar.forEach { ing ->
                agregarEvitar(repoIngrediente.buscar(ing).firstOrNull()
                    ?: throw RuntimeException("Ingrediente '$ing' no encontrado!"))
            }

            limpiarLocalesPreferidos()
            nuevoUsuarioDTO.restosFavoritos.map{
                agregarLocalPreferido(repositoryLocal.buscar(it.nombreLocal).first())
            }

            limpiarPalabrasMarketing()
            nuevoUsuarioDTO.palabrasMarketing.forEach { palabrasMarketing.add(it) }

            distanciaMax = nuevoUsuarioDTO.distanciaMax
        }


        repository.updateById(id, nuevoUsuario)
        return "OK"
    }

    fun confirmarPedido(localID: Int, userID: Int, pedidoDTO: PedidoCheckoutDTO): String {
        val currentUser = repository.getById(userID)
            ?: throw RuntimeException("No se encontró al usuario que está haciendo el pedido")

        val local = repositoryLocal.getById(localID)
            ?: throw RuntimeException("No se encontró el local con ID $localID")

        val platos = pedidoDTO.platos.map { platoDTO ->
            val platoOriginal = local.menu.find { it.id == platoDTO.id }
                ?: throw RuntimeException("Plato con id ${platoDTO.id} no encontrado en el local")

            Plato(
                nombre = platoOriginal.nombre,
                descripcion = platoOriginal.descripcion,
                ingredientes = platoOriginal.ingredientes,
                local = local,
                fechaLanzamiento = platoOriginal.fechaLanzamiento,
                valorBase = platoOriginal.valorBase
            ).apply {
                id = platoDTO.id
                cantidad = platoDTO.cantidad
                imagen = platoOriginal.imagen
            }
        }.toMutableList()

        val pedido = Pedido(
            cliente = currentUser,
            local = local,
            platos = platos,
            medioDePago = pedidoDTO.metodoPago,
            estadoDelPedido = pedidoDTO.estado
        )

        currentUser.confirmarPedido(pedido)

        pedidoDTO.copy(nombreLocal = local.nombreLocal)
        repository.updateById(userID, currentUser)
        return "OK"
    }
    fun getUsersPedidos(): List<UserWithPedidosDTO> {
        val users = repository.getAll()

        return users.map { user ->
            UserWithPedidosDTO(
                id = user.id,
                nombre = user.datos.nombre,
                pedidos = user.listaPedidos.map { pedido ->
                    BasicPedidoData(
                        local = pedido.local.nombreLocal,
                        platos = pedido.platos.map { plato -> plato.nombre },
                        metodoPago = pedido.medioDePago
                    )
                }
            )
        }
    }
    fun getUserPedidoById(id: Int): UserWithPedidosDTO {
        val user = repository.getById(id)

        if (user == null) throw RuntimeException("No se encontró el usuario con id $id")
        return UserWithPedidosDTO(
            id = user.id,
            nombre = user.datos.nombre,
            pedidos = user.listaPedidos.map { pedido ->
                BasicPedidoData(
                    local = pedido.local.nombreLocal,
                    platos = pedido.platos.map { plato -> plato.nombre },
                    metodoPago = pedido.medioDePago
                )
            }
        )
    }

    fun localesParaCalificar(userId: Int): List<Local> {
        val user = repository.getById(userId)
            ?: throw BusinessException("No se encontró un usuario con ID $userId")

        return user.listaPedidos
            .filter { it.estadoDelPedido === EstadoDelPedido.ENTREGADO }
            .map { it.local }
            .distinct()
    }

    fun estrellaDistancia(): Int = (5..45).random()

    fun getUserProfile(id: Int): UserUpdateDTO{
        val user = repository.getById(id)
        if(user == null) throw RuntimeException("No se encontró al usuario con id $id")

        val userDTO: UserUpdateDTO = UserUpdateDTO(
            nombre = user.datos.nombre,
            apellido = user.datos.apellido,
            direccion = user.ubicacion.calle,
            altura = user.ubicacion.altura,
            latitud = user.ubicacion.coordenadaY,
            longitud = user.ubicacion.coordenadaX,
            condicion = user.condicion,
            ingredientesEvitar = user.ingredientesAEvitar.map {it.nombre}.toMutableList(),
            ingredientesPreferidos = user.ingredientesPreferidos.map {it.nombre}.toMutableList(),
            restosFavoritos = user.localesPreferidos.map { local -> LocalPerfilDTO(
                nombreLocal = local.nombreLocal,
                imgPath = local.imgURL,
                distancia = user.ubicacion.coordenadas.distance(local.direccion.coordenadas),
                promedio = local.promedioPuntajes(),
                tipoDelivery = "Gratis",
                mediosDePago = local.mediosDePago
            ) }.toMutableList(),
            palabrasMarketing = user.palabrasMarketing.toMutableList(),
            distanciaMax = user.distanciaMax,
        )

        return userDTO
    }

    fun getResumenTemporal(localId: Int, userId: Int, pedidoFront: PedidoCheckoutDTO): ResumenPedidoDTO {

        val user = repository.getById(userId)
            ?: throw BusinessException("No existe usuario con ID $userId")

        val local = repoLocal.getById(localId)
            ?: throw BusinessException("No existe local con ID $localId")

        val platos = pedidoFront.platos.map { platoDTO ->
            val platoOriginal = local.menu.find { it.id == platoDTO.id }
                ?: throw RuntimeException("Plato con id ${platoDTO.id} no encontrado en el menú del local")

            Plato(
                nombre = platoOriginal.nombre,
                descripcion = platoOriginal.descripcion,
                ingredientes = platoOriginal.ingredientes,
                local = local,
                fechaLanzamiento = platoOriginal.fechaLanzamiento,
                valorBase = platoOriginal.valorBase
            ).apply {
                cantidad = platoDTO.cantidad
            }
        }

        // Crear un pedido TEMPORAL (no se guarda)
        val pedidoTemporal = Pedido(
            cliente = user,
            local = local,
            platos = platos.toMutableList(),
            medioDePago = pedidoFront.metodoPago,
            estadoDelPedido = pedidoFront.estado
        )
        val tienePlatos = pedidoTemporal.platos.isNotEmpty()

        if (!tienePlatos) {
            return ResumenPedidoDTO(
                recargo = 0.0,
                costeEnvio = 0.0,
                total = 0.0
            )
        }

        val costeEnvio = pedidoTemporal.costeDeEnvio().redondearADosDecimales()
        val total = pedidoTemporal.totalAPagar().redondearADosDecimales()
        val recargo = pedidoTemporal.adicionalPorMedio().redondearADosDecimales()

        return ResumenPedidoDTO(
            recargo = recargo,
            costeEnvio = costeEnvio,
            total = total
        )
    }
}

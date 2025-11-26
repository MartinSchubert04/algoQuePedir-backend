import cupon.Cupon
import algoQuePedir.domain.ActualizarIngredientes
import algoQuePedir.domain.AgregarLocales
import algoQuePedir.domain.BorrarCupones
import algoQuePedir.domain.BorrarMensajes
import algoQuePedir.domain.GrupoAlimenticio
import algoQuePedir.domain.Mail
import algoQuePedir.domain.MailSender
import algoQuePedir.domain.Mensaje
import algoQuePedir.domain.ServicioIngredientes
import algoQuePedir.domain.repoCupon
import algoQuePedir.domain.repoIngrediente
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.Ingrediente
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.every
import io.mockk.mockk
import io.kotest.core.spec.IsolationMode
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.verify
import java.time.LocalDateTime

fun mockedMailSender(): MailSender = mockk<MailSender>(relaxUnitFun = true)
val mockedMailSender = mockedMailSender()


val borrarMensajes = BorrarMensajes(mockedMailSender)

val borrarCupones = BorrarCupones(mockedMailSender)
val agregarLocales = AgregarLocales(mockedMailSender, listOf(pizzasYa, elFortin))





class AdminTest: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val cuponVencido = mockk<Cupon>()
    every { cuponVencido.vencido() } returns true
    every { cuponVencido.aplicado } returns false

    val servicio = ServicioIngredientes()
    val actIngredientes = ActualizarIngredientes(mockedMailSender, servicio)
    val ingredienteJson = Ingrediente(
        nombre = "Leche",
        costoMercado = 200.5,
        grupoAlimenticio = GrupoAlimenticio.LACTEOS,
        esDeOrigenAnimal = true
    ).apply { id = 22 }
    val mensaje = Mensaje(LocalDateTime.now().minusDays(31), "hola", "hola mundo", true)

    beforeTest {
        repoLocal.coleccion.clear()
        repoCupon.coleccion.clear()
        repoIngrediente.coleccion.clear()
        repoLocal.coleccion.forEach { local ->
             local.inbox.mensajes.clear() }
    }
    describe("Test instrucciones"){

        it("Borrar mensajes viejos y leidos"){
            repoLocal.coleccion.add(pizzasYa)
            pizzasYa.inbox.mensajes.add(mensaje)
            borrarMensajes.ejecutar()
            pizzasYa.inbox.mensajes shouldHaveSize 0
            verify(exactly = 1){
                mockedMailSender.sendMail(
                    Mail(
                        "Admin",
                        "admin@aqp.com.ar",
                        "Proceso ejecutado: Borrar Mensajes Antiguos y Leídos",
                        "Se realizó el proceso: Borrar Mensajes Antiguos y Leídos"
                    )
                )
            }
        }
        it("Actualizar repo de ingredientes"){
            actIngredientes.ejecutar()
            repoIngrediente.coleccion[0] shouldBeEqualToComparingFields  ingredienteJson
            verify(exactly = 1){
                mockedMailSender.sendMail(
                    Mail(
                        "Admin",
                        "admin@aqp.com.ar",
                        "Proceso ejecutado: Actualizar Ingredientes",
                        "Se realizó el proceso: Actualizar Ingredientes"
                    )
                )
            }
        }
        it("Borrar cupones vencidos y sin aplicar"){
            repoCupon.coleccion.add(cuponVencido)
            borrarCupones.ejecutar()
            repoCupon.coleccion shouldBe listOf()
            verify(exactly = 1){
                mockedMailSender.sendMail(
                    Mail(
                        "Admin",
                        "admin@aqp.com.ar",
                        "Proceso ejecutado: Borrar cupones vencidos sin aplicar",
                        "Se realizó el proceso: Borrar cupones vencidos sin aplicar"
                    )
                )
            }
        }
        it("Agregar muchos locales"){
            agregarLocales.ejecutar()
            repoLocal.coleccion shouldBe listOf(pizzasYa, elFortin)
            verify(exactly = 1){
                mockedMailSender.sendMail(
                    Mail(
                        "Admin",
                        "admin@aqp.com.ar",
                        "Proceso ejecutado: Agregar locales de forma masiva",
                        "Se realizó el proceso: Agregar locales de forma masiva"
                    )
                )
            }
        }
    }
})
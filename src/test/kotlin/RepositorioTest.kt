import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import algoQuePedir.domain.repoCupon
import algoQuePedir.domain.repoDelivery
import algoQuePedir.domain.repoIngrediente
import algoQuePedir.domain.repoLocal
import algoQuePedir.domain.repoPlato
import algoQuePedir.domain.repoUsuario
import algoQuePedir.domain.Ingrediente
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk


// val repositorioCupon = Repositorio<Cupon>()

// val busquedaCupon: CriterioBusqueda<Cupon, String> = CuponSearch()

class RepositorioTest : DescribeSpec({


    val carne = mockk<Ingrediente>()
    every {carne.id} returns 52
    every { carne.id = any() } just Runs  // Permitir el set
    every {carne.nombre} returns ("carne")

    val miel = mockk<Ingrediente>()
    every { miel.id } returns 101
    every { miel.id = any() } just Runs  // Permitir el set
    every { miel.nombre } returns ("miel")

    pizza.id = 102

    beforeTest {
        repoPlato.coleccion.clear()
        repoUsuario.coleccion.clear()
        repoDelivery.coleccion.clear()
        repoIngrediente.coleccion.clear()
        repoLocal.coleccion.clear()
        repoCupon.coleccion.clear()

        repoPlato.currentId = 0
        repoLocal.currentId = 0
        repoDelivery.currentId = 0
        repoIngrediente.currentId = 0
        repoDelivery.currentId = 0
        repoCupon.currentId = 0

        repoUsuario.create(userGus)
        repoLocal.create(pizzasYa)
        repoDelivery.create(delivery1)
        repoIngrediente.create(salchicha)
        repoIngrediente.create(carne)
        repoIngrediente.create(miel)
        repoPlato.create(pancho)
        repoPlato.create(pizza)
        repoCupon.create(unCuponSimple)
    }
    describe("Test acciones basicas") {
        beforeTest {
            repoPlato.coleccion.clear()
            repoPlato.currentId = 0
            repoPlato.create(pancho)
            repoPlato.create(pizza)
        }

        it("Crear objeto en colleccion"){
            pancho.id shouldNotBe null
            pizza.id shouldNotBe null
            repoPlato.coleccion shouldBe listOf(pancho, pizza)
        }

        it("Eliminar objeto en colleccion"){
            repoPlato.delete(pancho)
            repoPlato.coleccion shouldBe listOf(pizza)
        }
        it("Actualizar objeto en colleccion y checkeos de error en id"){
            repoPlato.update(pancho.apply { nombre = "panchooo"})
            repoPlato.coleccion[0].nombre shouldBe "panchooo"
        }
        it ("Encontrar objeto por id") {
            repoPlato.getById(pizza.id ?: throw RuntimeException("ID no asignado")) shouldBe pizza
        }
        it("Eliminar obj y verificar su id"){
            repoPlato.delete(pancho)
            pancho.id shouldBe null
        }

    }

    describe("Tipos de busqueda"){
        it ("Busqueda de un plato por string en un repositorio") {
            repoPlato.buscar("pan") shouldBe listOf(pancho) // comienzo de nombre
            repoPlato.buscar("con salchicha") shouldBe listOf(pancho) // por descripcion
        }
        it("Busqueda de usuario por string en un repositorio") {
            repoUsuario.buscar("gazzty") shouldBe listOf(userGus) // username exacto
            repoUsuario.buscar("gus") shouldBe listOf(userGus) // comienzo de nombre
            repoUsuario.buscar("muinos") shouldBe listOf(userGus) // busqueda apellido
        }
        it("Busqueda de delivery por string en un repositorio") {
            repoDelivery.buscar("testUs") shouldBe listOf(delivery1) // por como empieza username
        }
        it("Busqueda de local por string en un repositorio") {
            repoLocal.buscar("zzas") shouldBe listOf(pizzasYa)
            repoLocal.buscar("alcorta") shouldBe listOf(pizzasYa)
        }
        it("Busqueda de ingrediente en un repositorio") {
            repoIngrediente.buscar("salchicha") shouldBe listOf(salchicha)
        }
        it("Busqueda de cupon en un repositorio"){
            repoCupon.buscar(15.0) shouldBe listOf(unCuponSimple)
        }
    }
})
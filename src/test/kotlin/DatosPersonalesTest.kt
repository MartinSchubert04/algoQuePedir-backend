import algoQuePedir.domain.DatosPersonales
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

val datosGus = DatosPersonales(
    nombre = "Gustavo",
    apellido = "Muinos",
    username = "Gazzty",
    password = "abc123"
)


val datosAgus = DatosPersonales(
    nombre = "Agustin",
    apellido = "Gutierrez",
    username = "Guti71",
    password = "a0b1c3"
)

val datosUser1 = DatosPersonales(
    nombre = "nombre",
    apellido = "apellido",
    username = "user124143",
    password = "contraseñaresegura"
)
val datosUser2 = DatosPersonales(
    nombre = "Agustin",
    apellido = "Gutierrez",
    username = "guti12",
    password = "123abc"
)
val datosUser3 = DatosPersonales(
    nombre = "Nahuel",
    apellido = "Gutierrez",
    username = "guti21",
    password = "abc123"
)
val datosDel1 = DatosPersonales(
    nombre = "Delivery",
    apellido = "Test",
    username = "testUser",
    password = "password",
)
val datosDel2 = DatosPersonales(
    nombre = "Delivery",
    apellido = "Test",
    username = "testUser",
    password = "password",
)
val datosDel3 = DatosPersonales(
    nombre = "Delivery",
    apellido = "Test",
    username = "testUser",
    password = "password",
)


class DatosPersonalesTest: DescribeSpec({

    describe("Test info general de datos personales") {
        it("Test nombre") { datosGus.nombre shouldBe "Gustavo" }
        it("Test apellido") { datosGus.apellido shouldBe "Muinos" }
        it("Test username") { datosGus.username shouldBe "Gazzty" }
        it("Test password") { datosGus.password shouldBe "abc123" }
    }
})
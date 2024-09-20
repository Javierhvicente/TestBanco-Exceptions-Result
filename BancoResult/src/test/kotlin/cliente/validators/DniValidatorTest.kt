package cliente.validators

import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.Cliente
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.models.TarjetaCredito
import org.example.cliente.validators.DniValidator
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DniValidatorTest {
    private val validator = DniValidator()
    private val cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0)
    private val tarjetaCredito = TarjetaCredito("1234567890123456", "12/23")


    @Test
    @DisplayName("Validación del DNI correcto")
    fun casoOk() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "70919049K", cuenta, tarjetaCredito)
        val result = validator.validateDni(cliente)
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)
    }

    @Test
    @DisplayName("DNI incorrecto, más de 9 caracteres")
    fun mayorDe9() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "123456789Z", cuenta, tarjetaCredito)
        val result =  validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidDni)
    }

    @Test
    @DisplayName("DNI incorrecto, menos de 9 caracteres")
    fun menorDe9() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "1234567Z", cuenta, tarjetaCredito)
        val result =  validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidDni)
    }

    @Test
    @DisplayName("DNI incorrecto, solo letras")
    fun todoLetras() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "ZZZZZZZZZ", cuenta, tarjetaCredito)
        val result = validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidDni)
    }

    @Test
    @DisplayName("DNI incorrecto, solo números")
    fun todoNumeros() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "123456789", cuenta, tarjetaCredito)
        val result = validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidDni)
    }

    @Test
    @DisplayName("DNI incorrecto, letra incorrecta")
    fun letraIncorrecta() {
        val cliente = Cliente(UUID.randomUUID(), "Pepe", "12345678A", cuenta, tarjetaCredito)
        val result = validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidDni)
    }

    @Test
    @DisplayName("DNI incorrecto, nombre incorrecto por debajo de 3 caracteres")
    fun nombreIcncorrectoPorDebajoDelValorLimite(){
        val cliente = Cliente(UUID.randomUUID(), "Pe", "70919049K", cuenta, tarjetaCredito)
        val result = validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidName)
    }
    @Test
    @DisplayName("DNI incorrecto, nombre incorrecto por encima del valor limite")
    fun nombreIcncorrectoPorEncimaDelValorLimite() {
        val cliente = Cliente(
            UUID.randomUUID(),
            "Pesfdsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
            "70919049K",
            cuenta,
            tarjetaCredito
        )
        val result = validator.validateDni(cliente).error
        assertTrue(result is ValidatorError.InvalidName)
    }
}
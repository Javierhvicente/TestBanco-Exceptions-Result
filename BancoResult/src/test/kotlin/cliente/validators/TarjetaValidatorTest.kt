package cliente.validators

import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.TarjetaCredito
import org.example.cliente.validators.TarjetaValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TarjetaValidatorTest {
    private val tarjetaValidator = TarjetaValidator()
    @Test
    fun validateTarjetaCreditoOk() {
        val tarjeta = TarjetaCredito("4257 4600 0460 1637", "12/25")
        val resultado = tarjetaValidator.validateTarjetaCredito(tarjeta)
        assertEquals(tarjeta, resultado.value)
        assertTrue(resultado.isOk)
    }

    @Test
    fun validateTarjetaCreditoInvalidNumero() {
        val tarjeta = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidNumero)
    }

    @Test
    fun validateTarjetaNumeroNoSonDigitos(){
        val tarjeta = TarjetaCredito("lalalalalalalalal", "12/25")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidNumero)

    }

    @Test
    fun validateTarjetaNumeroPorDebajoDelValorMinimo(){
        val tarjeta = TarjetaCredito("123456789012", "12/25")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidNumero)
    }

    @Test
    fun validateTarjetaNumeroPorEncimaDelValorMaximo(){
        val tarjeta = TarjetaCredito("12345678901234567890", "12/25")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidNumero)
    }

    @Test
    fun validateTarjetaNumeroInvalido(){
        val tarjeta = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidNumero)

    }

    @Test
    fun invalidFechadeVencimiento(){
        val tarjeta = TarjetaCredito("4257 4600 0460 1637", "8/23")
        val result = tarjetaValidator.validateTarjetaCredito(tarjeta).error
        assertTrue(result is ValidatorError.InvalidFechaCaducidad)

    }


    @Test
    fun validateTarjetaVencidaPorEncimaDelValorlimite(){
        val tarjeta = TarjetaCredito("4257 4600 0460 1637", "10/25")
        val resultado = tarjetaValidator.validateTarjetaCredito(tarjeta)
        assertEquals(tarjeta, resultado.value)
        assertTrue(resultado.isOk)
    }
}
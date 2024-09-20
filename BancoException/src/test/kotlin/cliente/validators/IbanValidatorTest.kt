package cliente.validators

import org.example.cliente.exceptions.CuentaBancariaExceptions
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.validators.IbanValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IbanValidatorTest {
    private val validator = IbanValidator()
    private val iban = "ES9121000418450200051332"

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun validateCuentaOk(){
        val cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0)
        val result = validator.validateCuentaBancaria(cuenta)
        assertEquals(cuenta, result)
    }

    @Test
    fun validateCuentaErrorIbanShort(){
        val cuenta = CuentaBancaria("ES912100041845020005133", 10.0)
        val result = assertThrows<CuentaBancariaExceptions.InvalidIbanException> {
            validator.validateCuentaBancaria(cuenta)
        }
        assertEquals("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto", result.message)
    }

    @Test
    fun validateCuentaErrorIbanInvalido(){
        val cuenta = CuentaBancaria("ES6020385778987654321098", 10.0)
        val result = assertThrows<CuentaBancariaExceptions.InvalidIbanException> {
            validator.validateCuentaBancaria(cuenta)
        }
        assertEquals("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto", result.message)
    }

    @Test
    fun validaetCuentaErrorSueldoNegativo(){
        val cuenta = CuentaBancaria("ES9121000418450200051332", -1.0)
        val result = assertThrows<CuentaBancariaExceptions.InvalidSaldoException> {
            validator.validateCuentaBancaria(cuenta)
        }
        assertEquals("Cuenta bancaria inválida, el sueldo no puede ser menor a 0", result.message)
    }

    @Test
    fun validaetCuentaErrorSueldoPorEncimaDeCero(){
        val cuenta = CuentaBancaria("ES9121000418450200051332", 1.0)
        val result = validator.validateCuentaBancaria(cuenta)
        assertEquals(cuenta, result)
    }

}
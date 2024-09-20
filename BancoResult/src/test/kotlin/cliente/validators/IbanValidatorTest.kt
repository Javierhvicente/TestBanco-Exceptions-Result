package cliente.validators


import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.validators.IbanValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*

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
        assertEquals(cuenta, result.value)
        assertTrue { result.isOk }
    }

    @Test
    fun validateCuentaErrorIbanShort(){
        val cuenta = CuentaBancaria("ES912100041845020005133", 10.0)
        val result = validator.validateCuentaBancaria(cuenta).error
        assertTrue(result is ValidatorError.InvalidIban)
    }

    @Test
    fun validateCuentaErrorIbanInvalido(){
        val cuenta = CuentaBancaria("ES6020385778987654321098", 10.0)
        val result = validator.validateCuentaBancaria(cuenta).error
        assertTrue(result is ValidatorError.InvalidIban)
    }

    @Test
    fun validaetCuentaErrorSueldoNegativo(){
        val cuenta = CuentaBancaria("ES9121000418450200051332", -1.0)
        val result = validator.validateCuentaBancaria(cuenta).error
        assertTrue(result is ValidatorError.InvalidSaldo)
    }

    @Test
    fun validaetCuentaErrorSueldoPorEncimaDeCero(){
        val cuenta = CuentaBancaria("ES9121000418450200051332", 1.0)
        val result = validator.validateCuentaBancaria(cuenta)
        assertEquals(cuenta, result.value)
        assertTrue { result.isOk }
    }
}
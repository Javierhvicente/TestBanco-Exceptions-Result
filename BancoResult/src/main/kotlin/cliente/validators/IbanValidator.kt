package org.example.cliente.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.CuentaBancaria
import org.lighthousegames.logging.logging
import java.math.BigInteger

private val logger = logging()
class IbanValidator {
    fun validateCuentaBancaria(cuentaBancaria: CuentaBancaria): Result<CuentaBancaria, ValidatorError> {
        logger.debug { "Validando cuenta bancaria" }
         if(!validateIban(cuentaBancaria.iban)) {
             logger.error { "Cuenta bancaria inválida" }
             return Err(ValidatorError.InvalidIban("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto"))
         }
        if(!validateSaldo(cuentaBancaria.saldo)) {
            logger.error { "Cuenta bancaria inválida" }
            return Err(ValidatorError.InvalidSaldo("Cuenta bancaria inválida, el sueldo no puede ser menor a 0"))
        }
        return Ok(cuentaBancaria)
    }
    private fun validateIban(iban: String): Boolean {
        logger.debug { "Validando IBAN" }
        val iban = iban.replace(" ", "")
        if (iban.length != 24) {
            return false
        }
        val ibanOrdenado = iban.substring(4) + iban.substring(0, 4)
        val ibanCheck = StringBuilder()
        for (i in ibanOrdenado.indices){
            if(ibanOrdenado[i].isDigit()){
               ibanCheck.append(ibanOrdenado[i])
            }else{
                ibanCheck.append(ibanOrdenado[i].code - 55)
            }
        }
        val numeroIban = BigInteger(ibanCheck.toString())
        return numeroIban % BigInteger("97") == BigInteger("1")
    }

    private fun validateSaldo(saldo: Double): Boolean {
        logger.debug { "Validando sueldo" }
        if (saldo <= 0) {
            logger.error { "El saldo de la cuenta no puede ser menor a 0" }
            return false
        }
        logger.info { "Saldo válido" }
        return true
    }
}
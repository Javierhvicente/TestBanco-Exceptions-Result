package org.example.cliente.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.Cliente
import org.lighthousegames.logging.logging

private val logger = logging()
class DniValidator {
    fun validateDni(cliente: Cliente): Result<Cliente, ValidatorError> {
        if(!validateDniCodigo(cliente.Dni)) {
            return Err(ValidatorError.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas"))
        }
        if(!validateNombre(cliente.nombre)) {
            return Err(ValidatorError.InvalidName("Nombre inválido, el nombre debe tener entre 2 y 50 caracteres"))
        }
        return Ok(cliente)
    }
    private fun validateNombre(nombre: String): Boolean {
        return nombre.length in 3..50
    }
    private fun validateDniCodigo(dni: String): Boolean {
        val dniRegex = Regex("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")
        if(!dni.matches(dniRegex)) {
            logger.error { "DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas" }
            return false
        }
        val numeros = dni.slice(0..7).toInt()
        val letras = "TRWAGMYFPDXBNJZSQVHLCKE"
        val letra = letras[numeros % 23]
        return letra == dni.last()
    }
}
package org.example.cliente.validators

import org.example.cliente.exceptions.DniExceptions
import org.example.cliente.models.Cliente
import org.lighthousegames.logging.logging

private val logger = logging()
class DniValidator {
    fun validateDni(cliente: Cliente): Cliente {
        if(!validateDniCodigo(cliente.Dni)) {
            throw DniExceptions.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas")
        }
        if(!validateNombre(cliente.nombre)) {
            throw DniExceptions.InvalidName("Nombre inválido, el nombre debe tener entre 2 y 50 caracteres")
        }
        return cliente
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
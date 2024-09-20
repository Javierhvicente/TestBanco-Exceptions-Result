package org.example.cliente.errors

sealed class ValidatorError(message: String) {
    class InvalidIban(message: String): ValidatorError(message)
    class InvalidSaldo(message: String): ValidatorError(message)
    class InvalidDni(message: String): ValidatorError(message)
    class InvalidName(message: String): ValidatorError(message)
    class InvalidNumero(message: String): ValidatorError(message)
    class InvalidFechaCaducidad(message: String): ValidatorError(message)
}
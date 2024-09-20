package org.example.cliente.exceptions

sealed class DniExceptions(message: String) : RuntimeException(message) {
    class InvalidName(message: String) : DniExceptions(message)
    class InvalidDni(message: String) : DniExceptions(message)
}
package org.example.cliente.exceptions

sealed class TarjetaCreditoExceptions(message: String) : RuntimeException(message) {
    class InvalidFechaCaducidad(message: String) : TarjetaCreditoExceptions(message)
    class InvalidNumero(message: String) : TarjetaCreditoExceptions(message)
}

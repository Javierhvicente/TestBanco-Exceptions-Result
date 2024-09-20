package org.example.cliente.exceptions

sealed class ClienteServiceExceptions(message: String) : RuntimeException(message){
    class ClienteNotFoundException(message: String): ClienteServiceExceptions(message)
    class ClienteNotUpdatedException(message: String): ClienteServiceExceptions(message)
    class ClienteNotDeletedException(message: String): ClienteServiceExceptions(message)
}
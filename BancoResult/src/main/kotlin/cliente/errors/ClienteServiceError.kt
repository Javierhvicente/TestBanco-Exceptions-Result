package org.example.cliente.errors

sealed class ClienteServiceError(message: String){
    class NotFound(message: String): ClienteServiceError(message)
    class NotUpdated(message: String): ClienteServiceError(message)
    class NotDeleted(message: String): ClienteServiceError(message)
    class NotSaved(message: String): ClienteServiceError(message)
}
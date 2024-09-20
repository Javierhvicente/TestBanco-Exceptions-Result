package org.example.cliente.exceptions

sealed class CuentaBancariaExceptions(message: String) : RuntimeException(message){
    class InvalidIbanException(message: String) : CuentaBancariaExceptions(message)
    class InvalidSaldoException(message: String) : CuentaBancariaExceptions(message)
}
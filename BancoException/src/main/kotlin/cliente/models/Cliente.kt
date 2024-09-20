package org.example.cliente.models

import java.time.LocalDateTime
import java.util.*

data class Cliente(
    val id: UUID = UUID.randomUUID(),
    val nombre: String,
    val Dni: String,
    val cuentaBancaria: CuentaBancaria,
    val tarjetaCredito: TarjetaCredito,
    val updated_at: LocalDateTime = LocalDateTime.now(),
    val created_at: LocalDateTime = LocalDateTime.now()
) {

}
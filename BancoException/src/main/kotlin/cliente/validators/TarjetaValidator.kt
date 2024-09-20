package org.example.cliente.validators

import org.example.cliente.exceptions.TarjetaCreditoExceptions
import org.example.cliente.models.TarjetaCredito
import org.lighthousegames.logging.logging

private val logger = logging()
class TarjetaValidator {
    fun validateTarjetaCredito(tarjetaCredito: TarjetaCredito): TarjetaCredito {
        if(!validateNumero(tarjetaCredito.numero)) {
            throw TarjetaCreditoExceptions.InvalidNumero("El número de tarjeta no es válido")
        }
        if(!validateFechaVencimiento(tarjetaCredito.fechaVencimiento)) {
            throw TarjetaCreditoExceptions.InvalidFechaCaducidad("La fecha de vencimiento no es válida. Debe ser MM/YY y no puede estar caducada")
        }
        return tarjetaCredito
    }
    private fun validateNumero(numero: String): Boolean {
        logger.debug { "Validando número de tarjeta" }
        val tarjetaLimpia = numero.replace(" ", "")
        if (tarjetaLimpia.length !in 13..19) return false
        if (!tarjetaLimpia.all { it.isDigit() }) return false
        val numeros = tarjetaLimpia.map { it.toString().toInt() }.toIntArray()
        val suma = numeros.reversed().mapIndexed { index, digito ->
            if (index % 2 == 1) {
                val doble = digito * 2
                if (doble > 9) doble - 9 else doble
            } else {
                digito
            }
        }.sum()
        return suma % 10 == 0
    }

    private fun validateFechaVencimiento(fecha: String): Boolean {
        logger.debug { "Validando fecha de vencimiento" }
        var mes: Int = 0
        var año: Int = 0
        fecha.trim().split("/").let {
             mes = it[0].toInt()
             año = it[1].toInt()
        }
        if(mes !in 1..12 || año < 24) {
            logger.error { "La fecha de vencimiento no es válida. Mes incorrecto o año incorrecto" }
            return false
        }
        logger.info { "La fecha de vencimiento es válida" }
        return true
    }
}
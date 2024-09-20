package org.example.cliente.repositories

import org.example.cliente.models.Cliente
import org.lighthousegames.logging.logging
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()
class ClienteRepositoryImpl: ClienteRepository {
    private val db = mutableMapOf<UUID, Cliente>()
    override fun getAll(): List<Cliente> {
        logger.debug { "Getting all clients" }
        return db.values.toList()
    }

    override fun getByName(name: String): List<Cliente> {
        logger.debug { "Getting client by name: $name" }
        logger.info { "Total de coincidencias: ${db.values.count { it.nombre.contains(name, ignoreCase = true) }}" }
        return db.values.filter { it.nombre.contains(name, ignoreCase = true) }
    }

    override fun getById(id: UUID): Cliente? {
        logger.debug { "Getting client by id: $id" }
        val cliente = db[id]
        cliente?.let {
            logger.info { "Persona encontrada: $it" }
        } ?: run {
            logger.warn { "Persona con ID: $id no encontrada" }
        }
        return cliente
    }

    override fun create(cliente: Cliente): Cliente {
        logger.debug { "Creating client: $cliente" }
        val timeStamp = LocalDateTime.now()
        val newCliente = cliente.copy( nombre = cliente.nombre,
            tarjetaCredito = cliente.tarjetaCredito,
            cuentaBancaria = cliente.cuentaBancaria,
            Dni = cliente.Dni,
            updated_at = timeStamp,
            created_at = timeStamp)
        db[cliente.id] = newCliente
        logger.info { "Cliente creado: $cliente" }
        return cliente
    }

    override fun update(id: UUID, cliente: Cliente): Cliente? {
        logger.debug { "Updating client with id: $id" }
        val clienteToUpdate = getById(id) ?: return null
        val timeStamp = LocalDateTime.now()
        val updatedCliente = clienteToUpdate.copy(
            nombre = cliente.nombre,
            tarjetaCredito = cliente.tarjetaCredito,
            cuentaBancaria = cliente.cuentaBancaria,
            Dni = cliente.Dni,
            updated_at = timeStamp)
        db[clienteToUpdate.id] = updatedCliente
        logger.info { "Cliente actualizado: $clienteToUpdate" }
        return updatedCliente
    }

    override fun delete(id: UUID): Cliente? {
        logger.debug { "Deleting client with id: $id" }
        val cliente = db.remove(id)
        cliente?.let {
            logger.info { "Cliente eliminado: $cliente" }
        } ?: run {
            logger.warn { "Cliente con ID: $id no encontrado" }
            return null
        }
        return cliente
    }

}
package org.example.cliente.repositories

import org.example.cliente.models.Cliente
import java.util.UUID

interface ClienteRepository {
    fun getAll(): List<Cliente>
    fun getByName(name: String): List<Cliente>
    fun getById(id: UUID): Cliente?
    fun create(cliente: Cliente): Cliente
    fun update(id: UUID, cliente: Cliente): Cliente?
    fun delete(id: UUID): Cliente?
}
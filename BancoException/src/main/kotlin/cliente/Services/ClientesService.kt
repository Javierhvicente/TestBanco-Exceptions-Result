package org.example.cliente.Services

import org.example.cliente.models.Cliente
import java.util.*

interface ClientesService {
    fun getAll(): List<Cliente>
    fun findByName(name: String): List<Cliente>
    fun getById(id: UUID): Cliente
    fun save(cliente: Cliente): Cliente
    fun update(id: UUID, cliente: Cliente): Cliente
    fun delete(id: UUID): Cliente
}
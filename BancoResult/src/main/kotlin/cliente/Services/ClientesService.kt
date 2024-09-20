package org.example.cliente.Services

import com.github.michaelbull.result.Result
import org.example.cliente.errors.ClienteServiceError
import org.example.cliente.errors.ValidatorError
import org.example.cliente.models.Cliente
import java.util.*

interface ClientesService {
    fun getAll(): Result<List<Cliente>, ClienteServiceError>
    fun findByName(name: String): Result<List<Cliente>, ClienteServiceError>
    fun getById(id: UUID): Result<Cliente, ClienteServiceError>
    fun save(cliente: Cliente): Result<Cliente, ClienteServiceError>
    fun update(id: UUID, cliente: Cliente): Result<Cliente, ClienteServiceError>
    fun delete(id: UUID): Result<Cliente, ClienteServiceError>
}
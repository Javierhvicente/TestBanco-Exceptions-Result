package org.example.cliente.cache

import org.example.cliente.models.Cliente
import java.util.UUID

interface CacheCliente : Cache<UUID, Cliente> {
}
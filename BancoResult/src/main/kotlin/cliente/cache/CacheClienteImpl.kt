package org.example.cliente.cache

import org.example.cliente.models.Cliente
import java.util.*

class CacheClienteImpl: CacheCliente {
    private val cache: MutableMap<UUID, Cliente> = mutableMapOf()

    override fun get(key: UUID): Cliente? = cache[key]

    override fun put(key: UUID, value: Cliente) {
        cache[key] = value
    }

    override fun remove(key: UUID) {
        cache.remove(key)
    }

    override fun clear() = cache.clear()
    override fun size() = cache.size
}
package cliente.cache

import org.example.cliente.cache.CacheClienteImpl
import org.example.cliente.models.Cliente
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.models.TarjetaCredito
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class CacheClienteImplTest {
    private val cache = CacheClienteImpl()
    val cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0)
    val tarjetaCredito = TarjetaCredito("1234567890123456", "12/25")
    val cliente = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaCredito)
    val cliente2 = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaCredito)
    @BeforeEach
    fun setUp() {
        cache.clear()
    }

    @Test
    fun get() {
        cache.put(cliente.id, cliente)
        val result = cache.get(cliente.id)
        assertAll(
            { assertEquals(cache.size(), 1) },
            { assertNotNull(result) },
            { assertEquals(cliente, result) }
        )
    }
    @Test
    fun getNotFound() {
        cache.put(cliente.id, cliente)
        val result = cache.get(UUID.fromString("23d41191-8a78-4c02-9127-06e76b56af17"))

        assertAll(
            { assertEquals(cache.size(), 1) },
            { assertNull(result) }
        )
    }

    @Test
    fun put() {
        cache.put(cliente.id, cliente)
        val result = cache.get(cliente.id)

        assertAll(
            { assertEquals(cache.size(), 1) },
            { assertNotNull(result) },
            { assertEquals(cliente, result) }
        )
    }

    @Test
    fun remove() {
        cache.put(cliente.id, cliente)
        cache.remove(cliente.id)
        val result = cache.get(cliente.id)

        // Assert
        assertAll(
            { assertEquals(cache.size(), 0) },
            { assertNull(result) },
        )
    }

    @Test
    fun clear() {
        cache.put(cliente.id, cliente)
        cache.clear()
        assertAll(
            { assertEquals(cache.size(), 0) }
        )
    }

    @Test
    fun size() {
        cache.put(cliente.id, cliente)
        assertAll(
            { assertEquals(cache.size(), 1) }
        )
    }
}
package cliente.repositories

import org.example.cliente.models.Cliente
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.models.TarjetaCredito
import org.example.cliente.repositories.ClienteRepositoryImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClienteRepositoryImplTest {
    private var repository = ClienteRepositoryImpl()
    @BeforeEach
    fun setUp() {
        val cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0)
        val tarjetaCredito = TarjetaCredito("1234567890123456", "12/25")
        val cliente = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaCredito)
        val cliente2 = Cliente(UUID.fromString("1009777d-3155-44ca-a565-c8ede2ed2396"), "test2", "70919049K", cuenta, tarjetaCredito)
        repository = ClienteRepositoryImpl()
        repository.create(cliente)
        repository.create(cliente2)
    }
    @AfterEach
    fun tearDown() {
        repository = ClienteRepositoryImpl()
    }
    @Test
    fun getAll() {
        val result = repository.getAll()
        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result[0].id) },
            { assertEquals(UUID.fromString("1009777d-3155-44ca-a565-c8ede2ed2396"), result[1].id) }
        )
    }

    @Test
    fun getByName() {
        val result = repository.getByName("test1")
        assertAll(
            { assert(result.size == 1) },
            { assert(result[0].nombre == "test1") },
        )
    }

    @Test
    fun getByNameNotFound() {
        val result = repository.getByName("test3")
        assert(result.isEmpty())
    }

    @Test
    fun getById() {
        val id = UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a")
        val result = repository.getById(id)
        assertAll(
            {assertEquals(id, result?.id)},
            {assertEquals("test1", result?.nombre)}

        )
    }
    @Test
    fun getByIdNotFound() {
        val id = UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b")
        val result = repository.getById(id)
    }

    @Test
    fun create() {
        val cliente = Cliente(UUID.randomUUID(), "test3", "70919049K", CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0), TarjetaCredito("1234567890123456", "12/25") )
        val result = repository.create(cliente)
        assertAll(
            {assertEquals(cliente.id, result.id)},
            {assertEquals(cliente.nombre, result.nombre)}
        )
    }

    @Test
    fun update() {
        val id = UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a")
        val cliente = Cliente(id, "test3", "70919049K", CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0), TarjetaCredito("1234567890123456", "12/25"))
        val result = repository.update(id, cliente)
        assertAll(
            {assertEquals(id, result?.id)},
            {assertEquals("test3", result?.nombre)}
        )
    }

    @Test
    fun updateNotFound() {
        val id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f03")
        val cliente = Cliente(id, "test3", "70919049K", CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0), TarjetaCredito("1234567890123456", "12/25"))
        val result = repository.update(id, cliente)
        assertNull(result)
    }

    @Test
    fun delete() {
        val cliente = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0), TarjetaCredito("1234567890123456", "12/25"))
        val result = repository.delete(cliente.id)
        assertAll(
            { assert(result?.nombre == "test1") },
            { assert(result?.id == UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a")) }
        )
    }

    @Test
    fun deleteNotFound() {
        val cliente = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"), "test1", "70919049K", CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0), TarjetaCredito("1234567890123456", "12/25"))
        val result = repository.delete(cliente.id)
        assertNull(result)
    }
}
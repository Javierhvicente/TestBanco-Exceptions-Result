package cliente.Services

import org.example.cliente.Services.ClienteServiceImpl
import org.example.cliente.cache.CacheCliente
import org.example.cliente.exceptions.ClienteServiceExceptions
import org.example.cliente.exceptions.CuentaBancariaExceptions
import org.example.cliente.exceptions.DniExceptions
import org.example.cliente.exceptions.TarjetaCreditoExceptions
import org.example.cliente.models.Cliente
import org.example.cliente.models.CuentaBancaria
import org.example.cliente.models.TarjetaCredito
import org.example.cliente.repositories.ClienteRepository
import org.example.cliente.validators.DniValidator
import org.example.cliente.validators.IbanValidator
import org.example.cliente.validators.TarjetaValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)

class ClienteServiceImplTest {
    @Mock
    private lateinit var dniValidator: DniValidator
    @Mock
    private lateinit var tarjetaValidator: TarjetaValidator
    @Mock
    private lateinit var IbanValidator: IbanValidator
    @Mock
    private lateinit var clienteRepository: ClienteRepository
    @Mock
    private lateinit var cacheCliente: CacheCliente
    @InjectMocks
    private lateinit var clienteService: ClienteServiceImpl

    val cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332", 10.0)
    val tarjetaCredito = TarjetaCredito("1234567890123456", "12/25")
    val cliente = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaCredito)
    val cliente2 = Cliente(UUID.fromString("1009777d-3155-44ca-a565-c8ede2ed2396"), "test2", "70919049K", cuenta, tarjetaCredito)
    private val clienteList = listOf(cliente, cliente2)
    @Test
    fun getAll() {
        whenever(clienteRepository.getAll()).thenReturn(clienteList)
        val result = clienteService.getAll()
        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result[0].id) },
            { assertEquals(UUID.fromString("1009777d-3155-44ca-a565-c8ede2ed2396"), result[1].id) },
        )
        verify(clienteRepository, times(1)).getAll()
    }

    @Test
    fun findByName() {
        whenever(clienteRepository.getByName("test1") ).thenReturn(listOf(cliente))
        val result = clienteService.findByName("test1")
        assertAll(
            { assert(result.size == 1) },
            { assert(result[0].nombre == "test1") },
        )
        verify(clienteRepository, times(1)).getByName("test1")
    }
    @Test
    fun notFoundByName(){
        val name = "test3"
        whenever(clienteRepository.getByName(name) ).thenReturn(emptyList())
        val result = org.junit.jupiter.api.assertThrows<ClienteServiceExceptions.ClienteNotFoundException> {
            clienteService.findByName(name)
        }
        assertEquals("No se encontraron clientes con el nombre: $name", result.message)
        verify(clienteRepository, times(1)).getByName(name)
    }

    @Test
    fun getByIdInCache() {
        whenever (cacheCliente.get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(cliente)
        val result = clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.id) },
            { assertEquals("test1", result.nombre) },
        )
        verify(cacheCliente, times(1)).get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
    }
    @Test
    fun getByIdnotInCache(){
        whenever (cacheCliente.get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(null)
        whenever(clienteRepository.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(cliente)
        val result = clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.id) },
            { assertEquals("test1", result.nombre) },
        )
        verify(cacheCliente, times(1)).get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        verify(clienteRepository, times(1)).getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        verify(cacheCliente, times(1)).put(cliente.id, cliente)
    }
    @Test
    fun getByIdNotFound(){
       val id = (UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
        whenever (cacheCliente.get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))).thenReturn(null)
        whenever(clienteRepository.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))).thenReturn(null)

        val result = org.junit.jupiter.api.assertThrows<ClienteServiceExceptions.ClienteNotFoundException> {
            clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
        }
        assertEquals("Cliente no encontrado con id $id", result.message)

        verify(cacheCliente, times(1)).get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
        verify(clienteRepository, times(1)).getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
    }

    @Test
    fun save() {
        whenever(dniValidator.validateDni(cliente)).thenReturn(cliente)
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenReturn(tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenReturn(cuenta)
        whenever(clienteRepository.create(cliente)).thenReturn(cliente)
        val result = clienteService.save(cliente)
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.id) },
            { assertEquals("test1", result.nombre) },
        )
        verify(dniValidator, times(1)).validateDni(cliente)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
        verify(clienteRepository, times(1)).create(cliente)
    }

    @Test
    fun saveInvalidDni(){
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteTest)).thenThrow(DniExceptions.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas"))
        val result = org.junit.jupiter.api.assertThrows<DniExceptions.InvalidDni> {
            clienteService.save(clienteTest)
        }
        assertEquals("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas", result.message)
        verify(dniValidator, times(1)).validateDni(clienteTest)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteTest.tarjetaCredito)
    }

    @Test
    fun saveInvalidName(){
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "te", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteTest)).thenThrow(DniExceptions.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas"))
        val result = org.junit.jupiter.api.assertThrows<DniExceptions.InvalidDni> {
            clienteService.save(clienteTest)
        }
        assertEquals("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas", result.message)
        verify(dniValidator, times(1)).validateDni(clienteTest)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteTest.tarjetaCredito)
    }

    @Test
    fun saveInvalidTarjetaCreditoNumero() {
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenThrow(TarjetaCreditoExceptions.InvalidNumero("El número de tarjeta no es válido"))
        val result = org.junit.jupiter.api.assertThrows<TarjetaCreditoExceptions.InvalidNumero> {
            clienteService.save(cliente)
        }
        assertEquals("El número de tarjeta no es válido", result.message)

        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)
    }

    @Test
    fun saveInvalidTarjetaCreditoFecha() {
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1637", "12/20")
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenThrow(TarjetaCreditoExceptions.InvalidFechaCaducidad("La fecha de vencimiento no es válida"))
        val result = org.junit.jupiter.api.assertThrows<TarjetaCreditoExceptions.InvalidFechaCaducidad> {
            clienteService.save(cliente)
        }
        assertEquals("La fecha de vencimiento no es válida", result.message)

        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)

    }

    @Test
    fun saveInvalidIbanNumber(){
        val cuentaMal = CuentaBancaria("ES912100041845020005133", 10.0)
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenThrow(CuentaBancariaExceptions.InvalidIbanException("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto"))
        val result = org.junit.jupiter.api.assertThrows<CuentaBancariaExceptions.InvalidIbanException> {
            clienteService.save(cliente)
        }
        assertEquals("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto", result.message)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
    }

    @Test
    fun saveInvalidSaldo(){
        val cuentaMal = CuentaBancaria("ES9121000418450200051332", -1.0)
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenThrow(CuentaBancariaExceptions.InvalidSaldoException("Cuenta bancaria inválida, el sueldo no puede ser menor a 0"))
        val result = org.junit.jupiter.api.assertThrows<CuentaBancariaExceptions.InvalidSaldoException> {
            clienteService.save(cliente)
        }
        assertEquals("Cuenta bancaria inválida, el sueldo no puede ser menor a 0", result.message)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
    }

    @Test
    fun update() {
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(clienteUpdate)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(cuenta)
        doNothing().`when`(cacheCliente).remove(cliente.id)
        whenever(clienteRepository.update(clienteUpdate.id, clienteUpdate)).thenReturn(clienteUpdate)
        val result = clienteService.update(clienteUpdate.id, clienteUpdate)
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.id) },
            { assertEquals("test3", result.nombre) },
            {assertEquals("70919049L", result.Dni)},
        )
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
        verify(cacheCliente, times(1)).remove(clienteUpdate.id)
        verify(clienteRepository, times(1)).update(clienteUpdate.id, clienteUpdate)
    }

    @Test
    fun updateInvalidDni(){
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenThrow(DniExceptions.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas"))
        val result = org.junit.jupiter.api.assertThrows<DniExceptions.InvalidDni> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas", result.message)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
    }

    @Test
    fun updateInvalidNombrePorDebajoDelValorLimite(){
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "te", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenThrow(DniExceptions.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas"))
        val result = org.junit.jupiter.api.assertThrows<DniExceptions.InvalidDni> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas", result.message)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
    }

    @Test
    fun updateInvalidNombrePorEncimaDelValorLimite(){
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "ter4eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenThrow(DniExceptions.InvalidName("Nombre inválido, el nombre debe tener entre 3 y 50 caracteres"))
        val result = org.junit.jupiter.api.assertThrows<DniExceptions.InvalidName> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("Nombre inválido, el nombre debe tener entre 3 y 50 caracteres", result.message)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
    }
    @Test
    fun updateInvalidTarjetaNumero(){
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenThrow(TarjetaCreditoExceptions.InvalidNumero("El número de tarjeta no es válido"))
        val result = org.junit.jupiter.api.assertThrows<TarjetaCreditoExceptions.InvalidNumero> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("El número de tarjeta no es válido", result.message)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(cacheCliente, times(0)).remove(clienteUpdate.id)
    }

    @Test
    fun updateInvalidTarjetaFecha(){
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1637", "12/20")
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenThrow(TarjetaCreditoExceptions.InvalidFechaCaducidad("La fecha de vencimiento no es válida"))
        val result = org.junit.jupiter.api.assertThrows<TarjetaCreditoExceptions.InvalidFechaCaducidad> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("La fecha de vencimiento no es válida", result.message)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(cacheCliente, times(0)).remove(clienteUpdate.id)
    }

    @Test
    fun updateInvalidIban(){
        val cuentaMal = CuentaBancaria("ES912100041845020005133", 10.0)
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenThrow(CuentaBancariaExceptions.InvalidIbanException("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto"))
        val result = org.junit.jupiter.api.assertThrows<CuentaBancariaExceptions.InvalidIbanException> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto", result.message)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
    }

    @Test
    fun upadteInvalidCuentaSaldo() {
        val cuentaMal = CuentaBancaria("ES912100041845020005133", -1.0)
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenThrow(CuentaBancariaExceptions.InvalidSaldoException("El saldo de la cuenta no puede ser negativo"))
        val result = org.junit.jupiter.api.assertThrows<CuentaBancariaExceptions.InvalidSaldoException> {
            clienteService.update(clienteUpdate.id, clienteUpdate)
        }
        assertEquals("El saldo de la cuenta no puede ser negativo", result.message)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
    }

    @Test
    fun updateNotFound() {
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(clienteUpdate)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(cuenta)
        doNothing().`when`(cacheCliente).remove(cliente.id)
        whenever(clienteRepository.update(cliente.id, clienteUpdate)).thenReturn(null)

        val result = org.junit.jupiter.api.assertThrows<ClienteServiceExceptions.ClienteNotUpdatedException> {
            clienteService.update(cliente.id, clienteUpdate)
        }
        assertEquals("Persona no actualizada con ID: ${cliente.id}", result.message)

        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
        verify(cacheCliente, times(1)).remove(clienteUpdate.id)
        verify(clienteRepository, times(1)).update(clienteUpdate.id, clienteUpdate)
    }

    @Test
    fun deleteOk() {
        doNothing().`when`(cacheCliente).remove(cliente.id)
        whenever(clienteRepository.delete(cliente.id)).thenReturn(cliente)
        val result = clienteService.delete(cliente.id)
        assertAll(
            {assertEquals(cliente, result)} ,
            {assertEquals(cliente.id, result.id)}
        )
        verify(cacheCliente, times(1)).remove(cliente.id)
        verify(clienteRepository, times(1)).delete(cliente.id)
    }

    @Test
    fun deleteNotFound() {
        val id = UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b")
        whenever(clienteRepository.delete(id)).thenReturn(null)
        doNothing().`when`(cacheCliente).remove(id)
        val result = org.junit.jupiter.api.assertThrows<ClienteServiceExceptions.ClienteNotDeletedException> {
            clienteService.delete(id)
        }
        assertAll(
            {assertEquals("Persona no borrada con ID: ${id}", result.message)}
        )
        verify(cacheCliente, times(1)).remove(id)
        verify(clienteRepository, times(1)).delete(id)
    }
}
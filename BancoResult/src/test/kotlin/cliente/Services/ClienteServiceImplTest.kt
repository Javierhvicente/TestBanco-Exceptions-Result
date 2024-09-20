package cliente.Services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.example.cliente.Services.ClienteServiceImpl
import org.example.cliente.cache.CacheCliente
import org.example.cliente.errors.ClienteServiceError
import org.example.cliente.errors.ValidatorError
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
            { assertEquals(2, result.value.size) },
            { assertTrue(result.isOk) },
        )
        verify(clienteRepository, times(1)).getAll()
    }

    @Test
    fun findByName() {
        whenever(clienteRepository.getByName("test1") ).thenReturn(listOf(cliente))
        val result = clienteService.findByName("test1")
        assertAll(
            { assert(result.value.size == 1) },
            { assert(result.value[0].nombre == "test1") },
            { assertTrue(result.isOk) },
        )
        verify(clienteRepository, times(1)).getByName("test1")
    }
    @Test
    fun notFoundByName(){
        val name = "test3"
        whenever(clienteRepository.getByName(name) ).thenReturn(emptyList())
        val result = clienteService.findByName(name).error
        assertTrue(result is ClienteServiceError.NotFound)
        verify(clienteRepository, times(1)).getByName(name)
    }

    @Test
    fun getByIdInCache() {
        whenever (cacheCliente.get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(cliente)
        val result = clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.value.id) },
            { assertEquals("test1", result.value.nombre) },
        )
        verify(cacheCliente, times(1)).get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
    }
    @Test
    fun getByIdnotInCache(){
        whenever (cacheCliente.get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(null)
        whenever(clienteRepository.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))).thenReturn(cliente)
        val result = clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"))
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.value.id) },
            { assertEquals("test1", result.value.nombre) },
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

        val result = clienteService.getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b")).error
        assertTrue(result is ClienteServiceError.NotFound)

        verify(cacheCliente, times(1)).get(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
        verify(clienteRepository, times(1)).getById(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b"))
    }

    @Test
    fun save() {
        whenever(dniValidator.validateDni(cliente)).thenReturn(Ok(cliente))
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenReturn(Ok(tarjetaCredito))
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenReturn(Ok(cuenta))
        whenever(clienteRepository.create(cliente)).thenReturn(cliente)
        val result = clienteService.save(cliente)
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.value.id) },
            { assertEquals("test1", result.value.nombre) },
            { assertTrue(result.isOk) }
        )
        verify(dniValidator, times(1)).validateDni(cliente)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
        verify(clienteRepository, times(1)).create(cliente)
    }

    @Test
    fun saveInvalidDni(){
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteTest)).thenReturn(Err(ValidatorError.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas")))
        val result = clienteService.save(clienteTest).error
        assertTrue(result is ClienteServiceError.NotSaved)
        verify(dniValidator, times(1)).validateDni(clienteTest)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteTest.tarjetaCredito)
    }

    @Test
    fun saveInvalidName(){
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "te", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteTest)).thenReturn(Err(ValidatorError.InvalidName("Nombre inválido, el nombre debe tener entre 3 y 50 caracteres")))
        val result = clienteService.save(clienteTest).error
        assertTrue(result is ClienteServiceError.NotSaved)
        verify(dniValidator, times(1)).validateDni(clienteTest)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteTest.tarjetaCredito)
    }

    @Test
    fun saveInvalidTarjetaCreditoNumero() {
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenReturn(Err(ValidatorError.InvalidNumero("El número de tarjeta no es válido")))
        val result = clienteService.save(cliente).error
       assertTrue(result is ClienteServiceError.NotSaved)

        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)
    }

    @Test
    fun saveInvalidTarjetaCreditoFecha() {
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1637", "12/20")
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(cliente.tarjetaCredito)).thenReturn(Err(ValidatorError.InvalidFechaCaducidad("La fecha de vencimiento no es válida")))
        val result = clienteService.save(cliente).error
        assertTrue(result is ClienteServiceError.NotSaved)

        verify(tarjetaValidator, times(1)).validateTarjetaCredito(cliente.tarjetaCredito)

    }

    @Test
    fun saveInvalidIbanNumber(){
        val cuentaMal = CuentaBancaria("ES912100041845020005133", 10.0)
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenReturn(Err(ValidatorError.InvalidIban("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto")))
        val result = clienteService.save(cliente).error
        assertTrue(result is ClienteServiceError.NotSaved)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
    }

    @Test
    fun saveInvalidSaldo(){
        val cuentaMal = CuentaBancaria("ES9121000418450200051332", -1.0)
        val clienteTest = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(cliente.cuentaBancaria)).thenReturn(Err(ValidatorError.InvalidSaldo("Cuenta bancaria inválida, el sueldo no puede ser menor a 0")))
        val result = clienteService.save(cliente).error
        assertTrue(result is ClienteServiceError.NotSaved)
        verify(IbanValidator, times(1)).validateCuentaBancaria(cliente.cuentaBancaria)
    }

    @Test
    fun update() {
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(Ok(clienteUpdate))
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(Ok(tarjetaCredito))
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(Ok(cuenta))
        doNothing().`when`(cacheCliente).remove(cliente.id)
        whenever(clienteRepository.update(clienteUpdate.id, clienteUpdate)).thenReturn(clienteUpdate)
        val result = clienteService.update(clienteUpdate.id, clienteUpdate)
        assertAll(
            { assertEquals(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), result.value.id) },
            { assertEquals("test3", result.value.nombre) },
            {assertEquals("70919049L", result.value.Dni)},
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
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(Err(ValidatorError.InvalidDni("DNI inválido, el DNI debe estar compuesto de 8 digitos y una de las letras permitidas mayúsculas")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
    }

    @Test
    fun updateInvalidNombrePorDebajoDelValorLimite(){
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "te", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(Err(ValidatorError.InvalidName("Nombre inválido, el nombre debe tener entre 3 y 50 caracteres")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
        verify(tarjetaValidator, times(0)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
    }

    @Test
    fun updateInvalidNombrePorEncimaDelValorLimite(){
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "ter4eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "70919049K", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(Err(ValidatorError.InvalidName("Nombre inválido, el nombre debe tener entre 3 y 50 caracteres")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate)
        verify(dniValidator, times(1)).validateDni(clienteUpdate)
    }
    @Test
    fun updateInvalidTarjetaNumero(){
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1638", "12/25")
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(Err(ValidatorError.InvalidNumero("El número de tarjeta no es válido")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(cacheCliente, times(0)).remove(clienteUpdate.id)
    }

    @Test
    fun updateInvalidTarjetaFecha(){
        val tarjetaMal = TarjetaCredito("4257 4600 0460 1637", "12/20")
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaMal)
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(Err(ValidatorError.InvalidFechaCaducidad("La fecha de vencimiento no es válida")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(tarjetaValidator, times(1)).validateTarjetaCredito(clienteUpdate.tarjetaCredito)
        verify(cacheCliente, times(0)).remove(clienteUpdate.id)
    }

    @Test
    fun updateInvalidIban(){
        val cuentaMal = CuentaBancaria("ES912100041845020005133", 10.0)
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(Err(ValidatorError.InvalidIban("IBAN inválido, la longitud debe ser de 24 caracteres y el codigo de IBAN es incorrecto")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
    }

    @Test
    fun upadteInvalidCuentaSaldo() {
        val cuentaMal = CuentaBancaria("ES912100041845020005133", -1.0)
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test1", "70919049K", cuentaMal, tarjetaCredito)
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(Err(ValidatorError.InvalidIban("El saldo de la cuenta no puede ser negativo")))
        val result = clienteService.update(clienteUpdate.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotUpdated)
        verify(IbanValidator, times(1)).validateCuentaBancaria(clienteUpdate.cuentaBancaria)
    }

    @Test
    fun updateNotFound() {
        val clienteUpdate = Cliente(UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442a"), "test3", "70919049L", cuenta, tarjetaCredito)
        whenever(dniValidator.validateDni(clienteUpdate)).thenReturn(Ok(clienteUpdate))
        whenever(tarjetaValidator.validateTarjetaCredito(clienteUpdate.tarjetaCredito)).thenReturn(Ok(tarjetaCredito))
        whenever(IbanValidator.validateCuentaBancaria(clienteUpdate.cuentaBancaria)).thenReturn(Ok(cuenta))
        doNothing().`when`(cacheCliente).remove(cliente.id)
        whenever(clienteRepository.update(cliente.id, clienteUpdate)).thenReturn(null)

        val result = clienteService.update(cliente.id, clienteUpdate).error
        assertTrue(result is ClienteServiceError.NotFound)
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
            {assertEquals(cliente, result.value)},
            {assertEquals(cliente.id, result.value.id)},
            { assertTrue(result.isOk) },
        )
        verify(cacheCliente, times(1)).remove(cliente.id)
        verify(clienteRepository, times(1)).delete(cliente.id)
    }

    @Test
    fun deleteNotFound() {
        val id = UUID.fromString("10815af8-4ec7-4bc4-8300-8b1be6fb442b")
        whenever(clienteRepository.delete(id)).thenReturn(null)
        doNothing().`when`(cacheCliente).remove(id)
        val result = clienteService.delete(id).error
        assertTrue(result is ClienteServiceError.NotDeleted)
        verify(cacheCliente, times(1)).remove(id)
        verify(clienteRepository, times(1)).delete(id)
    }
}
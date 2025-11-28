package com.bank.client_service;

import com.bank.client_service.dto.ClientDTO;
import com.bank.client_service.entity.Client;
import com.bank.client_service.repository.ClientRepository;
import com.bank.client_service.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceApplicationTests {

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ClientService clientService;

	private Client testClient1;
	private Client testClient2;
	private Client testClient3;
	private ClientDTO testClientDTO1;
	private ClientDTO testClientDTO2;
	private ClientDTO testClientDTO3;

	@BeforeEach
	void setUp() {
		// Preparación de datos de prueba reutilizables
		testClient1 = new Client();
		testClient1.setId(1L);
		testClient1.setFullName("Juan Pérez");
		testClient1.setEmail("juan.perez@example.com");
		testClient1.setPhoneNumber("77123456");
		testClient1.setAddress("Av. Siempre Viva 123");
		testClient1.setDocumentNumber("1234567");
		testClient1.setDocumentType("CI");
		testClient1.setCreatedAt(LocalDateTime.now());
		testClient1.setUpdatedAt(LocalDateTime.now());

		testClient2 = new Client();
		testClient2.setId(2L);
		testClient2.setFullName("María García");
		testClient2.setEmail("maria.garcia@example.com");
		testClient2.setPhoneNumber("77654321");
		testClient2.setAddress("Calle Falsa 456");
		testClient2.setDocumentNumber("7654321");
		testClient2.setDocumentType("CI");
		testClient2.setCreatedAt(LocalDateTime.now());
		testClient2.setUpdatedAt(LocalDateTime.now());

		// Tercer Cliente para probar cuando Falla
		testClient3 = new Client();
		testClient3.setId(3L);
		testClient3.setFullName("Pedro García");
		testClient3.setEmail("pedro.garcia@example.com");
		testClient3.setPhoneNumber("77654321");
		testClient3.setAddress("Calle Falsa 333");
		testClient3.setDocumentNumber("69796678");
		testClient3.setDocumentType("CI");
		testClient3.setCreatedAt(LocalDateTime.now());
		testClient3.setUpdatedAt(LocalDateTime.now());

		testClientDTO1 = new ClientDTO();
		testClientDTO1.setId(1L);
		testClientDTO1.setFullName("Juan Pérez");
		testClientDTO1.setEmail("juan.perez@example.com");
		testClientDTO1.setPhoneNumber("77123456");
		testClientDTO1.setAddress("Av. Siempre Viva 123");
		testClientDTO1.setDocumentNumber("1234567");
		testClientDTO1.setDocumentType("CI");

		testClientDTO2 = new ClientDTO();
		testClientDTO2.setId(2L);
		testClientDTO2.setFullName("María García");
		testClientDTO2.setEmail("maria.garcia@example.com");
		testClientDTO2.setPhoneNumber("77654321");
		testClientDTO2.setAddress("Calle Falsa 456");
		testClientDTO2.setDocumentNumber("7654321");
		testClientDTO2.setDocumentType("CI");

		// Tercer Cliente para probar cuando Falla
		testClientDTO3 = new ClientDTO();
		testClientDTO3.setId(3L);
		testClientDTO3.setFullName("Pedro García");
		testClientDTO3.setEmail("pedro.garcia@example.com");
		testClientDTO3.setPhoneNumber("77654321");
		testClientDTO3.setAddress("Calle Falsa 333");
		testClientDTO3.setDocumentNumber("69796678");
		testClientDTO3.setDocumentType("CI");
	}

	/**
	 * PRUEBA 1: Verifica que getAllClients retorne una lista vacía cuando no hay
	 * clientes
	 */
	@Test
	void testGetAllClients_WhenEmpty_ReturnsEmptyList() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Configurar el mock para que retorne una lista vacía
		when(clientRepository.findAll()).thenReturn(Collections.emptyList());

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		List<ClientDTO> result = clientService.getAllClients();

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el resultado sea una lista vacía
		assertNotNull(result, "El resultado no debe ser null");
		assertTrue(result.isEmpty(), "La lista debe estar vacía");
		assertEquals(0, result.size(), "El tamaño de la lista debe ser 0");

		// Verificar que se llamó al método findAll del repositorio
		verify(clientRepository, times(1)).findAll();
	}

	/**
	 * PRUEBA 2: Verifica que getAllClients retorne todos los clientes correctamente
	 * mapeados
	 */
	@Test
	void testGetAllClients_WithMultipleClients_ReturnsAllClients() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Configurar el mock para retornar múltiples clientes
		List<Client> clients = Arrays.asList(testClient1, testClient2);
		when(clientRepository.findAll()).thenReturn(clients);

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		List<ClientDTO> result = clientService.getAllClients();

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que se retornaron todos los clientes
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(2, result.size(), "Debe retornar 2 clientes");

		// Validar el primer cliente
		assertEquals(testClient1.getId(), result.get(0).getId());
		assertEquals(testClient1.getFullName(), result.get(0).getFullName());
		assertEquals(testClient1.getEmail(), result.get(0).getEmail());

		// Validar el segundo cliente
		assertEquals(testClient2.getId(), result.get(1).getId());
		assertEquals(testClient2.getFullName(), result.get(1).getFullName());
		assertEquals(testClient2.getEmail(), result.get(1).getEmail());

		// Verificar interacción con el repositorio
		verify(clientRepository, times(1)).findAll();
	}

	/**
	 * PRUEBA 3: Verifica que getClientById retorne el DTO correcto para un cliente
	 * existente
	 */
	@Test
	void testGetClientById_WhenExists_ReturnsClientDTO() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Configurar el mock para retornar un cliente existente
		when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient1));

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		ClientDTO result = clientService.getClientById(1L);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el resultado no sea null y contenga los datos correctos
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(testClient1.getId(), result.getId(), "El ID debe coincidir");
		assertEquals(testClient1.getFullName(), result.getFullName(), "El nombre completo debe coincidir");
		assertEquals(testClient1.getEmail(), result.getEmail(), "El email debe coincidir");
		assertEquals(testClient1.getPhoneNumber(), result.getPhoneNumber(), "El teléfono debe coincidir");
		assertEquals(testClient1.getAddress(), result.getAddress(), "La dirección debe coincidir");
		assertEquals(testClient1.getDocumentNumber(), result.getDocumentNumber(),
				"El número de documento debe coincidir");
		assertEquals(testClient1.getDocumentType(), result.getDocumentType(), "El tipo de documento debe coincidir");

		// Verificar que se llamó al método findById con el ID correcto
		verify(clientRepository, times(1)).findById(1L);
	}

	/**
	 * PRUEBA 4: Verifica que getClientById retorne null cuando el cliente no existe
	 */
	@Test
	void testGetClientById_WhenNotExists_ReturnsNull() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Configurar el mock para retornar Optional vacío
		when(clientRepository.findById(999L)).thenReturn(Optional.empty());

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba con un ID inexistente
		ClientDTO result = clientService.getClientById(999L);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el resultado sea null
		assertNull(result, "El resultado debe ser null cuando el cliente no existe");

		// Verificar que se intentó buscar el cliente
		verify(clientRepository, times(1)).findById(999L);
	}

	/**
	 * PRUEBA 5: Verifica que createClient guarde y retorne el nuevo cliente
	 * correctamente
	 */
	@Test
	void testCreateClient_WithValidData_ReturnsCreatedClient() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Crear DTO de entrada sin ID
		ClientDTO inputDTO = new ClientDTO();
		inputDTO.setFullName("Carlos Rodríguez");
		inputDTO.setEmail("carlos.rodriguez@example.com");
		inputDTO.setPhoneNumber("77111222");
		inputDTO.setAddress("Zona Norte 789");
		inputDTO.setDocumentNumber("9876543");
		inputDTO.setDocumentType("CI");

		// Configurar el mock para retornar el cliente guardado con ID
		Client savedClient = new Client();
		savedClient.setId(3L);
		savedClient.setFullName(inputDTO.getFullName());
		savedClient.setEmail(inputDTO.getEmail());
		savedClient.setPhoneNumber(inputDTO.getPhoneNumber());
		savedClient.setAddress(inputDTO.getAddress());
		savedClient.setDocumentNumber(inputDTO.getDocumentNumber());
		savedClient.setDocumentType(inputDTO.getDocumentType());
		savedClient.setCreatedAt(LocalDateTime.now());
		savedClient.setUpdatedAt(LocalDateTime.now());

		when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		ClientDTO result = clientService.createClient(inputDTO);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el cliente fue creado correctamente
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(3L, result.getId(), "El ID debe ser asignado");
		assertEquals(inputDTO.getFullName(), result.getFullName(), "El nombre debe coincidir");
		assertEquals(inputDTO.getEmail(), result.getEmail(), "El email debe coincidir");
		assertEquals(inputDTO.getPhoneNumber(), result.getPhoneNumber(), "El teléfono debe coincidir");
		assertEquals(inputDTO.getAddress(), result.getAddress(), "La dirección debe coincidir");
		assertEquals(inputDTO.getDocumentNumber(), result.getDocumentNumber(), "El número de documento debe coincidir");
		assertEquals(inputDTO.getDocumentType(), result.getDocumentType(), "El tipo de documento debe coincidir");

		// Verificar que se llamó al método save del repositorio
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	/**
	 * PRUEBA 6: Verifica que updateClient actualice correctamente un cliente
	 * existente
	 */
	@Test
	void testUpdateClient_WhenExists_ReturnsUpdatedClient() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Crear DTO con datos actualizados
		ClientDTO updateDTO = new ClientDTO();
		updateDTO.setFullName("Juan Pérez Actualizado");
		updateDTO.setEmail("juan.perez.nuevo@example.com");
		updateDTO.setPhoneNumber("77999888");
		updateDTO.setAddress("Nueva Dirección 999");
		updateDTO.setDocumentNumber("1234567");
		updateDTO.setDocumentType("CI");

		// Configurar el mock para encontrar el cliente existente
		when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient1));

		// Configurar el mock para retornar el cliente actualizado
		Client updatedClient = new Client();
		updatedClient.setId(1L);
		updatedClient.setFullName(updateDTO.getFullName());
		updatedClient.setEmail(updateDTO.getEmail());
		updatedClient.setPhoneNumber(updateDTO.getPhoneNumber());
		updatedClient.setAddress(updateDTO.getAddress());
		updatedClient.setDocumentNumber(updateDTO.getDocumentNumber());
		updatedClient.setDocumentType(updateDTO.getDocumentType());

		when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		ClientDTO result = clientService.updateClient(1L, updateDTO);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el cliente fue actualizado correctamente
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(1L, result.getId(), "El ID debe mantenerse");
		assertEquals(updateDTO.getFullName(), result.getFullName(), "El nombre debe estar actualizado");
		assertEquals(updateDTO.getEmail(), result.getEmail(), "El email debe estar actualizado");
		assertEquals(updateDTO.getPhoneNumber(), result.getPhoneNumber(), "El teléfono debe estar actualizado");
		assertEquals(updateDTO.getAddress(), result.getAddress(), "La dirección debe estar actualizada");

		// Verificar interacciones con el repositorio
		verify(clientRepository, times(1)).findById(1L);
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	/**
	 * PRUEBA 7: Verifica que updateClient retorne null cuando el cliente no existe
	 */
	@Test
	void testUpdateClient_WhenNotExists_ReturnsNull() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Crear DTO con datos de actualización
		ClientDTO updateDTO = new ClientDTO();
		updateDTO.setFullName("No Existe");
		updateDTO.setEmail("noexiste@example.com");
		updateDTO.setPhoneNumber("77000000");
		updateDTO.setAddress("Sin Dirección");
		updateDTO.setDocumentNumber("0000000");
		updateDTO.setDocumentType("CI");

		// Configurar el mock para no encontrar el cliente
		when(clientRepository.findById(999L)).thenReturn(Optional.empty());

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba con un ID inexistente
		ClientDTO result = clientService.updateClient(999L, updateDTO);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que el resultado sea null
		assertNull(result, "El resultado debe ser null cuando el cliente no existe");

		// Verificar que se intentó buscar el cliente pero no se guardó
		verify(clientRepository, times(1)).findById(999L);
		verify(clientRepository, never()).save(any(Client.class));
	}

	/**
	 * PRUEBA 8: Verifica que deleteClient invoque el método deleteById del
	 * repositorio
	 */
	@Test
	void testDeleteClient_WhenExists_CallsRepositoryDelete() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Configurar el mock para no lanzar excepciones al eliminar
		doNothing().when(clientRepository).deleteById(1L);

		// ========== LÓGICA (Act) ==========
		// Ejecutar el método bajo prueba
		clientService.deleteClient(1L);

		// ========== VERIFICACIÓN (Assert) ==========
		// Verificar que se llamó al método deleteById con el ID correcto
		verify(clientRepository, times(1)).deleteById(1L);
	}

	/**
	 * PRUEBA 9: Verifica indirectamente que mapToEntity convierta correctamente DTO
	 * a entidad
	 */
	@Test
	void testCreateClient_VerifiesMapToEntity_ConversionCorrectness() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Crear DTO completo con todos los campos
		ClientDTO inputDTO = new ClientDTO();
		inputDTO.setFullName("Ana López");
		inputDTO.setEmail("ana.lopez@example.com");
		inputDTO.setPhoneNumber("77222333");
		inputDTO.setAddress("Zona Sur 321");
		inputDTO.setDocumentNumber("5551234");
		inputDTO.setDocumentType("PASAPORTE");

		// Configurar el mock para capturar la entidad guardada
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			Client savedClient = invocation.getArgument(0);
			savedClient.setId(4L);
			savedClient.setCreatedAt(LocalDateTime.now());
			savedClient.setUpdatedAt(LocalDateTime.now());
			return savedClient;
		});

		// ========== LÓGICA (Act) ==========
		// Ejecutar createClient que internamente usa mapToEntity
		ClientDTO result = clientService.createClient(inputDTO);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que la conversión DTO -> Entidad fue correcta verificando el
		// resultado
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(inputDTO.getFullName(), result.getFullName(), "El nombre debe conservarse en la conversión");
		assertEquals(inputDTO.getEmail(), result.getEmail(), "El email debe conservarse en la conversión");
		assertEquals(inputDTO.getPhoneNumber(), result.getPhoneNumber(),
				"El teléfono debe conservarse en la conversión");
		assertEquals(inputDTO.getAddress(), result.getAddress(), "La dirección debe conservarse en la conversión");
		assertEquals(inputDTO.getDocumentNumber(), result.getDocumentNumber(),
				"El documento debe conservarse en la conversión");
		assertEquals(inputDTO.getDocumentType(), result.getDocumentType(),
				"El tipo de documento debe conservarse en la conversión");

		// Verificar que save fue llamado (indirectamente valida que mapToEntity
		// funcionó)
		verify(clientRepository, times(1)).save(any(Client.class));
	}

	/**
	 * PRUEBA 10: Verifica indirectamente que mapToDTO convierta correctamente
	 * entidad a DTO
	 */
	@Test
	void testGetClientById_VerifiesMapToDTO_ConversionCorrectness() {
		// ========== PREPARACIÓN (Arrange) ==========
		// Crear una entidad completa con timestamps
		Client clientWithTimestamps = new Client();
		clientWithTimestamps.setId(5L);
		clientWithTimestamps.setFullName("Pedro Martínez");
		clientWithTimestamps.setEmail("pedro.martinez@example.com");
		clientWithTimestamps.setPhoneNumber("77444555");
		clientWithTimestamps.setAddress("Zona Este 654");
		clientWithTimestamps.setDocumentNumber("8887777");
		clientWithTimestamps.setDocumentType("CI");
		clientWithTimestamps.setCreatedAt(LocalDateTime.now().minusDays(10));
		clientWithTimestamps.setUpdatedAt(LocalDateTime.now());

		// Configurar el mock para retornar la entidad
		when(clientRepository.findById(5L)).thenReturn(Optional.of(clientWithTimestamps));

		// ========== LÓGICA (Act) ==========
		// Ejecutar getClientById que internamente usa mapToDTO
		ClientDTO result = clientService.getClientById(5L);

		// ========== VERIFICACIÓN (Assert) ==========
		// Validar que la conversión Entidad -> DTO fue correcta
		assertNotNull(result, "El resultado no debe ser null");
		assertEquals(clientWithTimestamps.getId(), result.getId(), "El ID debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getFullName(), result.getFullName(), "El nombre debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getEmail(), result.getEmail(), "El email debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getPhoneNumber(), result.getPhoneNumber(),
				"El teléfono debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getAddress(), result.getAddress(),
				"La dirección debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getDocumentNumber(), result.getDocumentNumber(),
				"El documento debe mapearse correctamente");
		assertEquals(clientWithTimestamps.getDocumentType(), result.getDocumentType(),
				"El tipo de documento debe mapearse correctamente");

		// Nota: Los timestamps no deben estar en el DTO (validación del mapeo
		// selectivo)
		// Esto se verifica implícitamente ya que ClientDTO no tiene campos
		// createdAt/updatedAt

		// Verificar que findById fue llamado
		verify(clientRepository, times(1)).findById(5L);
	}
}

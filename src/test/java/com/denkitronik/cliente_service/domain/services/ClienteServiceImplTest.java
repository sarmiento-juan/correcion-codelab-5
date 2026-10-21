package com.denkitronik.cliente_service.domain.services;

import com.denkitronik.cliente_service.domain.entities.Cliente;
import com.denkitronik.cliente_service.domain.entities.Region;
import com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException;
import com.denkitronik.cliente_service.domain.exception.ClienteServiceException;
import com.denkitronik.cliente_service.domain.repositories.IClienteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de ClienteServiceImpl")
class ClienteServiceImplTest {

    @Mock
    private IClienteDao clienteDao;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setNombre("Sudamérica");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@kernel.org");
        cliente.setRegion(region);
    }

    @Test
    @DisplayName("debe buscar un cliente por id")
    void buscarPorIdDebeRetornarCliente() {
        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertThat(resultado).isSameAs(cliente);
        verify(clienteDao).findById(1L);
    }

    @Test
    @DisplayName("debe lanzar excepción cuando el cliente no existe")
    void buscarPorIdDebeLanzarExcepcionSiNoExiste() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("debe guardar un cliente")
    void saveDebeRetornarClienteGuardado() {
        when(clienteDao.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.save(cliente);

        assertThat(resultado).isSameAs(cliente);
        verify(clienteDao).save(cliente);
    }

    @Test
    @DisplayName("debe convertir errores de integridad al guardar")
    void saveDebeConvertirErrorDeIntegridad() {
        when(clienteDao.save(cliente))
                .thenThrow(new DataIntegrityViolationException("email duplicado"));

        assertThatThrownBy(() -> clienteService.save(cliente))
                .isInstanceOf(ClienteServiceException.class)
                .hasMessageContaining("datos duplicados");
    }

    @Test
    @DisplayName("debe actualizar los datos del cliente")
    void updateDebeActualizarYGuardar() {
        Cliente cambios = new Cliente();
        cambios.setNombre("Grace");
        cambios.setApellido("Hopper");
        cambios.setEmail("grace@navy.mil");
        cambios.setFoto("grace.jpg");
        cambios.setRegion(region);

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteDao.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.update(1L, cambios);

        assertThat(resultado).isSameAs(cliente);
        assertThat(cliente.getNombre()).isEqualTo("Grace");
        assertThat(cliente.getApellido()).isEqualTo("Hopper");
        assertThat(cliente.getEmail()).isEqualTo("grace@navy.mil");
        assertThat(cliente.getFoto()).isEqualTo("grace.jpg");
        verify(clienteDao).save(cliente);
    }

    @Test
    @DisplayName("debe lanzar excepción al actualizar un cliente inexistente")
    void updateDebeLanzarExcepcionSiNoExiste() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.update(99L, cliente))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(clienteDao, never()).save(any());
    }

    @Test
    @DisplayName("debe eliminar un cliente existente")
    void deleteDebeEliminarCliente() {
        when(clienteDao.existsById(1L)).thenReturn(true);

        clienteService.delete(1L);

        verify(clienteDao).deleteById(1L);
    }

    @Test
    @DisplayName("debe lanzar excepción al eliminar un cliente inexistente")
    void deleteDebeLanzarExcepcionSiNoExiste() {
        when(clienteDao.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.delete(99L))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(clienteDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("debe obtener todas las regiones")
    void findAllRegionesDebeRetornarRegiones() {
        when(clienteDao.findAllRegiones()).thenReturn(List.of(region));

        assertThat(clienteService.findAllRegiones()).containsExactly(region);
        verify(clienteDao).findAllRegiones();
    }
}

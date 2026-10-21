package com.denkitronik.cliente_service.domain.repositories;

import com.denkitronik.cliente_service.domain.entities.Cliente;
import com.denkitronik.cliente_service.domain.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Pruebas de integración de IClienteDao con H2")
class IClienteDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IClienteDao clienteDao;

    private Region region;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setNombre("Asia");
        entityManager.persist(region);

        cliente = new Cliente();
        cliente.setNombre("Grace");
        cliente.setApellido("Hopper");
        cliente.setEmail("grace@test.com");
        cliente.setRegion(region);
        entityManager.persist(cliente);
        entityManager.flush();
    }

    @Test
    @DisplayName("debe guardar y encontrar un cliente por id")
    void debeGuardarYEncontrarPorId() {
        Optional<Cliente> resultado = clienteDao.findById(cliente.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Grace");
        assertThat(resultado.get().getEmail()).isEqualTo("grace@test.com");
    }

    @Test
    @DisplayName("debe listar clientes con paginación")
    void debeListarClientesPaginados() {
        Page<Cliente> resultado = clienteDao.findAll(PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).extracting(Cliente::getEmail)
                .containsExactly("grace@test.com");
    }

    @Test
    @DisplayName("debe consultar todas las regiones")
    void debeListarRegiones() {
        List<Region> resultado = clienteDao.findAllRegiones();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Asia");
    }

    @Test
    @DisplayName("debe eliminar un cliente")
    void debeEliminarCliente() {
        Long id = cliente.getId();

        clienteDao.deleteById(id);
        entityManager.flush();

        assertThat(clienteDao.findById(id)).isEmpty();
    }
}

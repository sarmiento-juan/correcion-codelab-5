package com.denkitronik.cliente_service.delivery.rest;

import com.denkitronik.cliente_service.domain.entities.Cliente;
import com.denkitronik.cliente_service.domain.entities.Region;
import com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException;
import com.denkitronik.cliente_service.domain.exception.ClienteServiceException;
import com.denkitronik.cliente_service.domain.services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteRestController.class)
@DisplayName("Pruebas MVC de ClienteRestController")
class ClienteRestControllerTest {

    private static final String BASE_URL = "/api/v1/cliente-service";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IClienteService clienteService;

    private Cliente cliente;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setNombre("Europa");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Ada");
        cliente.setApellido("Lovelace");
        cliente.setEmail("ada@babbage.uk");
        cliente.setRegion(region);
    }

    @Test
    @DisplayName("debe listar clientes")
    void listarClientes() throws Exception {
        when(clienteService.findAll()).thenReturn(List.of(cliente));

        mockMvc.perform(get(BASE_URL + "/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ada"));
    }

    @Test
    @DisplayName("debe listar clientes paginados")
    void listarClientesPaginado() throws Exception {
        when(clienteService.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(cliente), PageRequest.of(0, 4), 1));

        mockMvc.perform(get(BASE_URL + "/clientes/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("ada@babbage.uk"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("debe buscar un cliente por id")
    void buscarCliente() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        mockMvc.perform(get(BASE_URL + "/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ada"));
    }

    @Test
    @DisplayName("debe devolver 404 cuando no existe el cliente")
    void buscarClienteInexistente() throws Exception {
        when(clienteService.buscarPorId(99L)).thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(get(BASE_URL + "/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("debe crear un cliente")
    void crearCliente() throws Exception {
        when(clienteService.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post(BASE_URL + "/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("debe rechazar un cliente inválido")
    void crearClienteInvalido() throws Exception {
        Cliente invalido = new Cliente();
        invalido.setApellido("Sin nombre");
        invalido.setEmail("correo-invalido");

        mockMvc.perform(post(BASE_URL + "/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido": "Sin nombre",
                                  "email": "correo-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());

        verify(clienteService, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("debe actualizar un cliente")
    void actualizarCliente() throws Exception {
        when(clienteService.update(eq(1L), any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put(BASE_URL + "/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ada@babbage.uk"));
    }

    @Test
    @DisplayName("debe eliminar un cliente")
    void eliminarCliente() throws Exception {
        doNothing().when(clienteService).delete(1L);

        mockMvc.perform(delete(BASE_URL + "/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).delete(1L);
    }

    @Test
    @DisplayName("debe listar regiones")
    void listarRegiones() throws Exception {
        when(clienteService.findAllRegiones()).thenReturn(List.of(region));

        mockMvc.perform(get(BASE_URL + "/clientes/regiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Europa"));
    }

    @Test
    @DisplayName("debe devolver 500 si falla el servicio")
    void errorDelServicio() throws Exception {
        when(clienteService.findAll())
                .thenThrow(new ClienteServiceException("fallo de base de datos"));

        mockMvc.perform(get(BASE_URL + "/clientes"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    private String clienteJson() {
        return """
                {
                  "id": 1,
                  "nombre": "Ada",
                  "apellido": "Lovelace",
                  "email": "ada@babbage.uk",
                  "region": {
                    "id": 1,
                    "nombre": "Europa"
                  }
                }
                """;
    }
}

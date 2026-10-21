package com.denkitronik.cliente_service.domain.services;

import com.denkitronik.cliente_service.domain.entities.Cliente;
import com.denkitronik.cliente_service.domain.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IClienteService {

    /**
     * Lista todos los clientes con paginación.
     */
    Page<Cliente> findAll(Pageable pageable);

    /**
     * Devuelve todos los clientes sin paginación.
     */
    List<Cliente> findAll();

    /**
     * Busca un cliente por ID.
     * @throws com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException
     *         si no existe un cliente con ese ID
     */
    Cliente buscarPorId(Long id);

    /**
     * Persiste un nuevo cliente.
     * @throws com.denkitronik.cliente_service.domain.exception.ClienteServiceException
     *         si ocurre un error de base de datos
     */
    Cliente save(Cliente cliente);

    /**
     * Actualiza los campos de un cliente existente preservando createAt.
     * @throws com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException
     *         si no existe un cliente con ese ID
     */
    Cliente update(Long id, Cliente cliente);

    /**
     * Elimina un cliente por ID.
     * @throws com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException
     *         si no existe un cliente con ese ID
     */
    void delete(Long id);

    /**
     * Devuelve todas las regiones disponibles.
     */
    List<Region> findAllRegiones();
}
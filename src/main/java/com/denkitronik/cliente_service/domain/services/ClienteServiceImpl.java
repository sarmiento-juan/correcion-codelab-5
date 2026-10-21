package com.denkitronik.cliente_service.domain.services;

import com.denkitronik.cliente_service.domain.exception.ClienteNotFoundException;
import com.denkitronik.cliente_service.domain.exception.ClienteServiceException;
import com.denkitronik.cliente_service.domain.entities.Cliente;
import com.denkitronik.cliente_service.domain.entities.Region;
import com.denkitronik.cliente_service.domain.repositories.IClienteDao;
import com.denkitronik.cliente_service.domain.repositories.IRegionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {

    @Autowired
    private IClienteDao clienteDao;

    @Autowired
    private IRegionDao regionDao;

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteDao.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        try {
            asociarRegionGestionada(cliente);
            return clienteDao.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new ClienteServiceException(
                    "Error al guardar el cliente: datos duplicados o restricción violada",
                    ex
            );
        }
    }

    @Override
    @Transactional
    public Cliente update(Long id, Cliente cliente) {
        Cliente actual = clienteDao.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        actual.setNombre(cliente.getNombre());
        actual.setApellido(cliente.getApellido());
        actual.setEmail(cliente.getEmail());
        actual.setFoto(cliente.getFoto());
        asociarRegionGestionada(cliente);
        actual.setRegion(cliente.getRegion());
        return clienteDao.save(actual);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clienteDao.existsById(id)) {
            throw new ClienteNotFoundException(id);
        }
        clienteDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Region> findAllRegiones() {
        return clienteDao.findAllRegiones();
    }

    private void asociarRegionGestionada(Cliente cliente) {
        if (regionDao == null || cliente.getRegion() == null
                || cliente.getRegion().getId() == null) {
            return;
        }

        regionDao.findById(cliente.getRegion().getId())
                .ifPresent(cliente::setRegion);
    }
}
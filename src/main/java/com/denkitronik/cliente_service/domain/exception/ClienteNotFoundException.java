package com.denkitronik.cliente_service.domain.exception;

public class ClienteNotFoundException extends RuntimeException {

    private final Long id;

    public ClienteNotFoundException(Long id) {
        super("El cliente con id " + id + " no encontrado");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

package com.denkitronik.cliente_service.domain.exception;

public class ClienteServiceException extends RuntimeException {

    public ClienteServiceException(String mensaje) {
        super(mensaje);
    }

    public ClienteServiceException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

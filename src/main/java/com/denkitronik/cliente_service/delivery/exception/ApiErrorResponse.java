package com.denkitronik.cliente_service.delivery.exception;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String mensaje;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

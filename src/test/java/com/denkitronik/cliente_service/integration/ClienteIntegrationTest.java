package com.denkitronik.cliente_service.integration;

import com.denkitronik.cliente_service.ClienteServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ClienteServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@DisplayName("Pruebas E2E con PostgreSQL y HTTP")
class ClienteIntegrationTest {

    private static final String BASE_URL = "/api/v1/cliente-service";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    private Long regionId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM clientes");
        jdbcTemplate.update("DELETE FROM regiones");
        jdbcTemplate.update("INSERT INTO regiones (nombre) VALUES (?)", "Europa");
        regionId = jdbcTemplate.queryForObject(
                "SELECT id FROM regiones WHERE nombre = ?",
                Long.class,
                "Europa"
        );
    }

    @Test
    @DisplayName("debe crear, consultar y eliminar un cliente mediante HTTP")
    void flujoCompletoDeCliente() throws Exception {
        String clienteJson = """
                {
                  "nombre": "Grace",
                  "apellido": "Hopper",
                  "email": "grace.e2e@test.com",
                  "region": {
                    "id": %d
                  }
                }
                """.formatted(regionId);

        HttpResponse<String> creado = send("POST", "/clientes", clienteJson);

        assertThat(creado.statusCode()).isEqualTo(201);
        assertThat(creado.body()).contains("\"email\":\"grace.e2e@test.com\"");

        Matcher idMatcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(creado.body());
        assertThat(idMatcher.find()).isTrue();
        String clienteId = idMatcher.group(1);

        HttpResponse<String> encontrado = send("GET", "/clientes/" + clienteId, null);
        assertThat(encontrado.statusCode()).isEqualTo(200);
        assertThat(encontrado.body()).contains("\"nombre\":\"Grace\"");

        HttpResponse<String> eliminado = send("DELETE", "/clientes/" + clienteId, null);
        assertThat(eliminado.statusCode()).isEqualTo(204);

        assertThat(send("GET", "/clientes/" + clienteId, null).statusCode())
                .isEqualTo(404);
    }

    @Test
    @DisplayName("debe listar las regiones mediante HTTP")
    void debeListarRegiones() throws Exception {
        HttpResponse<String> respuesta = send("GET", "/clientes/regiones", null);

        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).contains("\"nombre\":\"Europa\"");
    }

    private HttpResponse<String> send(String method, String path, String body)
            throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + BASE_URL + path))
                .header("Content-Type", "application/json");

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }

        return HttpClient.newHttpClient().send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }
}

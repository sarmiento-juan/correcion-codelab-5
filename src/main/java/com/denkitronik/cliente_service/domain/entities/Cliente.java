package com.denkitronik.cliente_service.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "Debe ser una dirección de email válida")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    private String foto;

    @NotNull(message = "La región es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Region region;

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
    }
}
package com.example.clinicarebackend.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("_id")
    private Long id; // Modificado para Long
    private String name;
    private String email;
    private String password;
    private String role;
    private String gender;

    private String cpf;
    private String telefone;
    private String datanasc;
    private String sangue;
    private String foto;
}

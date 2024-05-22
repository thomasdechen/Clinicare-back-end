package com.example.clinicarebackend.domain.user;

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
    private Long id; // Modificado para Long
    private String name;
    private String email;
    private String password;
    private String role;
    private String gender;
}

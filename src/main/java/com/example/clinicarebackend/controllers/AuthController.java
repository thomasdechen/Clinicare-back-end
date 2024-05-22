package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.Infra.security.TokenService;
import com.example.clinicarebackend.domain.codigomedicoa.CodigoMedicoa;
import com.example.clinicarebackend.domain.codigosecretarioa.CodigoSecretarioa;
import com.example.clinicarebackend.domain.user.User;
import com.example.clinicarebackend.dto.LoginRequestDTO;
import com.example.clinicarebackend.dto.RegisterRequestDTO;
import com.example.clinicarebackend.dto.ResponseDTO;
import com.example.clinicarebackend.repositories.UserRepository;
import com.example.clinicarebackend.repositories.CodigoMedicoaRepository;
import com.example.clinicarebackend.repositories.CodigoSecretarioaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final CodigoMedicoaRepository repositoryCodigoMedico;
    private final CodigoSecretarioaRepository repositoryCodigoSecretario;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> existingUser = this.repository.findByEmail(body.email());

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email já registrado.");
        }

        // Verifica o código médico antes de criar o novo usuário
        if ("medico".equals(body.role())) {
            if (body.codigo() != null) {
                Optional<CodigoMedicoa> codigoMedico = this.repositoryCodigoMedico.findByCodigo(body.codigo());
                if (codigoMedico.isEmpty()) {
                    return ResponseEntity.badRequest().body("Código inválido para médico.");
                }
                if (codigoMedico.get().getMedico() != null) {
                    return ResponseEntity.badRequest().body("Código já utilizado.");
                }
            } else {
                return ResponseEntity.badRequest().body("Código é obrigatório para médicos.");
            }
        } else if ("secretario".equals(body.role())) {
            if (body.codigo() != null) {
                Optional<CodigoSecretarioa> codigoSecretario = this.repositoryCodigoSecretario.findByCodigo(body.codigo());
                if (codigoSecretario.isEmpty()) {
                    return ResponseEntity.badRequest().body("Código inválido para secretário.");
                }
                if (codigoSecretario.get().getMedico() == null) {
                    return ResponseEntity.badRequest().body("Código de secretário não associado a nenhum médico.");
                }
            } else {
                return ResponseEntity.badRequest().body("Código é obrigatório para secretários.");
            }
        }

        // Cria e salva o novo usuário
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setEmail(body.email());
        newUser.setName(body.name());
        newUser.setRole(body.role());
        newUser.setGender(body.gender());
        this.repository.save(newUser);

        // Atualiza o código médico com o ID do novo médico e gera código de secretário
        if ("medico".equals(body.role()) && body.codigo() != null) {
            Optional<CodigoMedicoa> codigoMedico = this.repositoryCodigoMedico.findByCodigo(body.codigo());
            if (codigoMedico.isPresent()) {
                codigoMedico.get().setMedico(newUser);
                this.repositoryCodigoMedico.save(codigoMedico.get());

                // Gera um novo código para secretário
                CodigoSecretarioa codigoSecretario = new CodigoSecretarioa();
                codigoSecretario.setMedico(newUser);
                codigoSecretario.setCodigo(generateRandomCode());
                codigoSecretario.setTipo("secretario");
                this.repositoryCodigoSecretario.save(codigoSecretario);
            }
        }

        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
    }

    private int generateRandomCode() {
        Random random = new Random();
        int code;
        do {
            code = 10000 + random.nextInt(90000); // Gera um número de 5 dígitos
        } while (repositoryCodigoSecretario.findByCodigo(code).isPresent());
        return code;
    }
}

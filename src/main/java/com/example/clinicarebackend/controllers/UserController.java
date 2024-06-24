package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.domain.user.User;
import com.example.clinicarebackend.repositories.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injete o PasswordEncoder aqui


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        String email = authentication.name();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(""); // Não retornar a senha
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getUserProfileById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(""); // Não retornar a senha
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User userProfile) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userProfile.getName());
            user.setEmail(userProfile.getEmail());
            user.setGender(userProfile.getGender());
            user.setCpf(userProfile.getCpf());
            user.setTelefone(userProfile.getTelefone());
            user.setDatanasc(userProfile.getDatanasc());
            user.setSangue(userProfile.getSangue());
            user.setFoto(userProfile.getFoto());
            // Verifique se foi fornecida uma nova senha para atualização
            if (!userProfile.getPassword().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(userProfile.getPassword());
                user.setPassword(encryptedPassword);
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }


}

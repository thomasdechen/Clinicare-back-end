package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.domain.agendamento.Agendamento;
import com.example.clinicarebackend.domain.servicos.DisponibilidadeService;
import com.example.clinicarebackend.repositories.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {
    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @PostMapping("/criar")
    public ResponseEntity criarAgendamento2(@RequestBody Agendamento agendamento) {
        try {
            if (agendamento.getPreco() == null) {
                agendamento.setPreco(new BigDecimal(100.00)); // Definir um valor padrão, se necessário
            }
            Agendamento savedAgendamento = agendamentoRepository.save(agendamento);
            disponibilidadeService.atualizarDisponibilidadeAposAgendamento(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAgendamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar agendamento: " + e.getMessage());
        }
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Agendamento>> getAgendamentosDoPaciente(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoRepository.findByIdPaciente(id);
        if (agendamentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(agendamentos);
    }

    // Outros métodos de endpoint, se necessário
}
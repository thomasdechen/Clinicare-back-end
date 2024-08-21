package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.domain.agendamento.Agendamento;
import com.example.clinicarebackend.domain.servicos.DisponibilidadeService;
import com.example.clinicarebackend.repositories.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

        // Ordenar os agendamentos com base no status, data e hora (do mais distante para o mais próximo)
        agendamentos.sort((a1, a2) -> {
            int statusComparison = compareStatus(a1.getStatus(), a2.getStatus());
            if (statusComparison != 0) {
                return statusComparison;
            } else {
                int dateComparison = a2.getDia().compareTo(a1.getDia());
                if (dateComparison != 0) {
                    return dateComparison;
                } else {
                    return a2.getHora().compareTo(a1.getHora());
                }
            }
        });

        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/medico/{id}")
    public ResponseEntity<List<Agendamento>> getAgendamentosDoMedico(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoRepository.findByIdMedico(id);
        if (agendamentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Ordenar os agendamentos com base no status, data e hora (do mais distante para o mais próximo)
        agendamentos.sort((a1, a2) -> {
            int statusComparison = compareStatus(a1.getStatus(), a2.getStatus());
            if (statusComparison != 0) {
                return statusComparison;
            } else {
                int dateComparison = a2.getDia().compareTo(a1.getDia());
                if (dateComparison != 0) {
                    return dateComparison;
                } else {
                    return a2.getHora().compareTo(a1.getHora());
                }
            }
        });

        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/secretario/{medicoId}")
    public ResponseEntity<List<Agendamento>> getAgendamentosDosMedicosDoSecretario(@PathVariable Long medicoId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByIdMedico(medicoId);
        if (agendamentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Ordenar os agendamentos com base no status, data e hora (do mais distante para o mais próximo)
        agendamentos.sort((a1, a2) -> {
            int statusComparison = compareStatus(a1.getStatus(), a2.getStatus());
            if (statusComparison != 0) {
                return statusComparison;
            } else {
                int dateComparison = a2.getDia().compareTo(a1.getDia());
                if (dateComparison != 0) {
                    return dateComparison;
                } else {
                    return a2.getHora().compareTo(a1.getHora());
                }
            }
        });

        return ResponseEntity.ok(agendamentos);
    }

    // Método auxiliar para comparar o status dos agendamentos
    private int compareStatus(String status1, String status2) {
        if ("Cancelado".equals(status1)) {
            return -1;
        } else if ("Cancelado".equals(status2)) {
            return 1;
        } else {
            return status1.compareTo(status2);
        }
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<String> cancelarAgendamento(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoRepository.findById(id).orElse(null);
            if (agendamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento não encontrado");
            }

            // Verifica se o status já não é 'Cancelado'
            if ("Cancelado".equals(agendamento.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O agendamento já está cancelado");
            }

            agendamento.setStatus("Cancelado");
            agendamentoRepository.save(agendamento);

            return ResponseEntity.ok("Agendamento cancelado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cancelar o agendamento: " + e.getMessage());
        }
    }



    // Outros métodos de endpoint, se necessário
}
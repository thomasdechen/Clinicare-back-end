package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.domain.agendamento.Agendamento;
import com.example.clinicarebackend.domain.medico.Medico;
import com.example.clinicarebackend.domain.paciente.Paciente;
import com.example.clinicarebackend.repositories.AgendamentoRepository;
import com.example.clinicarebackend.repositories.MedicoRepository;
import com.example.clinicarebackend.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping("/criar")
    public ResponseEntity<?> criarAgendamento(@RequestBody Agendamento agendamentoRequest) {
        try {
            Medico medico = medicoRepository.findById(agendamentoRequest.getMedico().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
            Paciente paciente = pacienteRepository.findById(agendamentoRequest.getPaciente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

            Agendamento novoAgendamento = new Agendamento();
            novoAgendamento.setMedico(agendamentoRequest.getMedico());
            novoAgendamento.setPaciente(agendamentoRequest.getPaciente());
            novoAgendamento.setDia(agendamentoRequest.getDia());
            novoAgendamento.setHora(agendamentoRequest.getHora());
            novoAgendamento.setPreco(agendamentoRequest.getPreco());
            novoAgendamento.setStatus(agendamentoRequest.getStatus());
            novoAgendamento.setLocal(agendamentoRequest.getLocal());
            novoAgendamento.setNomeMedico(agendamentoRequest.getNomeMedico());
            novoAgendamento.setEspecialidadeMedico(agendamentoRequest.getEspecialidadeMedico());

            Agendamento savedAgendamento = agendamentoRepository.save(novoAgendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAgendamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/medico/{id}/dia/{data}")
    public ResponseEntity<List<Agendamento>> getAgendamentosByMedicoAndDia(@PathVariable Long id, @PathVariable String data) {
        try {
            LocalDate dia = LocalDate.parse(data); // Converte a string para LocalDate
            List<Agendamento> agendamentos = agendamentoRepository.findByMedicoIdAndDia(id, dia);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> getAgendamentoById(@PathVariable Long id) {
        Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
        return agendamentoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Agendamento> atualizarAgendamento(@PathVariable Long id, @RequestBody Agendamento agendamentoRequest) {
        Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
        if (agendamentoOptional.isPresent()) {
            try {
                Medico medico = medicoRepository.findById(agendamentoRequest.getMedico().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
                Paciente paciente = pacienteRepository.findById(agendamentoRequest.getPaciente().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

                Agendamento existingAgendamento = agendamentoOptional.get();
                existingAgendamento.setMedico(medico);
                existingAgendamento.setPaciente(paciente);
                existingAgendamento.setDia(agendamentoRequest.getDia());
                existingAgendamento.setHora(agendamentoRequest.getHora());
                existingAgendamento.setPreco(agendamentoRequest.getPreco());
                existingAgendamento.setStatus(agendamentoRequest.getStatus());
                existingAgendamento.setLocal(agendamentoRequest.getLocal());
                existingAgendamento.setNomeMedico(agendamentoRequest.getNomeMedico());
                existingAgendamento.setEspecialidadeMedico(agendamentoRequest.getEspecialidadeMedico());

                Agendamento updatedAgendamento = agendamentoRepository.save(existingAgendamento);
                return ResponseEntity.ok(updatedAgendamento);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarAgendamento(@PathVariable Long id) {
        Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
        if (agendamentoOptional.isPresent()) {
            agendamentoRepository.delete(agendamentoOptional.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

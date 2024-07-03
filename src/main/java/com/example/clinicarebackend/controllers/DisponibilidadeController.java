package com.example.clinicarebackend.controllers;

import com.example.clinicarebackend.domain.disponibilidade.Disponibilidade;
import com.example.clinicarebackend.domain.servicos.DisponibilidadeService;
import com.example.clinicarebackend.repositories.DisponibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @GetMapping("/medico/{id}/dias")
    public Set<String> getAvailableDates(@PathVariable Long id) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(45);
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByMedicoIdAndDiaBetween(id, hoje, limite);

        return disponibilidades.stream()
                .filter(Disponibilidade::isDisponivel)
                .map(d -> d.getDia().toString())
                .collect(Collectors.toSet());
    }

    @GetMapping("/medico/{id}/dia/{dia}")
    public List<String> getAvailableTimes(@PathVariable Long id, @PathVariable String dia) {
        LocalDate data = LocalDate.parse(dia);
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByMedicoIdAndDia(id, data);

        return disponibilidades.stream()
                .filter(Disponibilidade::isDisponivel)
                .map(d -> d.getHoraInicio().toString())
                .collect(Collectors.toList());
    }

    @PostMapping("/atualizar/{id}")
    public void atualizarDisponibilidade(@PathVariable Long id) {
        disponibilidadeService.verificarEAtualizarDisponibilidadePorMedico(id);
    }
}

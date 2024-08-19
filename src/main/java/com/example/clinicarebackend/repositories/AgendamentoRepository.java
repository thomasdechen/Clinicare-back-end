package com.example.clinicarebackend.repositories;

import com.example.clinicarebackend.domain.agendamento.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByMedicoIdAndDia(Long medicoId, LocalDate dia);
}

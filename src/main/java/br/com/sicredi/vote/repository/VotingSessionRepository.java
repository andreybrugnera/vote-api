package br.com.sicredi.vote.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sicredi.vote.model.VotingSession;
import br.com.sicredi.vote.model.VotingSessionStatus;

public interface VotingSessionRepository extends JpaRepository<VotingSession, UUID> {

    Optional<VotingSession> findByAgendaId(UUID agendaId);

    List<VotingSession> findAllByOrderByCreatedAtDesc();

    List<VotingSession> findByStatusAndClosesAtBefore(VotingSessionStatus status, LocalDateTime dateTime);

}

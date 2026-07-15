package br.com.sicredi.vote.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.sicredi.vote.dto.VotingSessionRequestDTO;
import br.com.sicredi.vote.dto.VotingSessionResponseDTO;
import br.com.sicredi.vote.model.VotingSession;

@Component
public class VotingSessionConverter extends DefaultConverter
        implements Converter<VotingSession, VotingSessionRequestDTO, VotingSessionResponseDTO> {

    @Override
    public VotingSessionResponseDTO convertToDto(VotingSession votingSession) {
        if (votingSession == null) {
            return null;
        }

        return VotingSessionResponseDTO.builder()
                .id(votingSession.getId())
                .agendaId(votingSession.getAgenda() != null ? votingSession.getAgenda().getId() : null)
                .openedAt(votingSession.getOpenedAt())
                .closesAt(votingSession.getClosesAt())
                .resultPublishedAt(votingSession.getResultPublishedAt())
                .createdAt(votingSession.getCreatedAt())
                .build();
    }

    @Override
    public VotingSession convertFromDto(VotingSessionRequestDTO votingSessionRequestDTO) {
        if (votingSessionRequestDTO == null) {
            return null;
        }

        return VotingSession.builder()
                .openedAt(votingSessionRequestDTO.getOpenedAt())
                .closesAt(votingSessionRequestDTO.getClosesAt())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

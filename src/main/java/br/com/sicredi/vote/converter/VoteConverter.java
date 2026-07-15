package br.com.sicredi.vote.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.model.Vote;

@Component
public class VoteConverter extends DefaultConverter
        implements Converter<Vote, VoteRequestDTO, VoteResponseDTO> {

    public static final String VOTE_REGISTERED_MESSAGE = "Your vote has been successfully registered";

    @Override
    public VoteResponseDTO convertToDto(Vote vote) {
        if (vote == null) {
            return null;
        }

        return VoteResponseDTO.builder()
                .message(VOTE_REGISTERED_MESSAGE)
                .id(vote.getId())
                .votingSessionId(vote.getVotingSession() != null ? vote.getVotingSession().getId() : null)
                .memberId(vote.getMember() != null ? vote.getMember().getId() : null)
                .choice(vote.getChoice())
                .createdAt(vote.getCreatedAt())
                .build();
    }

    @Override
    public Vote convertFromDto(VoteRequestDTO voteRequestDTO) {
        if (voteRequestDTO == null) {
            return null;
        }

        return Vote.builder()
                .choice(voteRequestDTO.getChoice())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

package br.com.sicredi.vote.repository;

import br.com.sicredi.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    boolean existsByVotingSessionIdAndMemberId(UUID votingSessionId, UUID memberId);
}

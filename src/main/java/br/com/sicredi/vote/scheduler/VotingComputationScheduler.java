package br.com.sicredi.vote.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.sicredi.vote.service.VotingSessionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class VotingComputationScheduler {

    private static final long EVERY_TEN_MINUTES = 10 * 60 * 1000L;

    private VotingSessionService votingSessionService;

    @Scheduled(fixedRate = EVERY_TEN_MINUTES)
    public void computeVotingSessions() {
        log.info("Computing voting sessions results");
        votingSessionService.computeVotingSessionsResult();
    }
}

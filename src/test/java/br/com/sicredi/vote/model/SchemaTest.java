package br.com.sicredi.vote.model;

import br.com.sicredi.vote.repository.AgendaRepository;
import br.com.sicredi.vote.repository.MemberRepository;
import br.com.sicredi.vote.repository.VoteRepository;
import br.com.sicredi.vote.repository.VotingSessionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SchemaTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void persistsAndReadsBackTheFullVotingChain() {
        Member member = saveMember();
        Agenda agenda = saveAgenda();
        VotingSession session = saveSession(agenda);
        Vote vote = saveVote(session, member, VoteChoice.YES);

        Vote persistedVote = voteRepository.findById(vote.getId()).orElseThrow();

        assertThat(persistedVote.getChoice()).isEqualTo(VoteChoice.YES);
        assertThat(persistedVote.getMember().getId()).isEqualTo(member.getId());
        assertThat(persistedVote.getVotingSession().getAgenda().getId()).isEqualTo(agenda.getId());
        assertThat(memberRepository.findById(member.getId()).orElseThrow().getType())
                .isEqualTo(MemberType.ASSOCIATE);
    }

    @Test
    void rejectsSecondSessionForTheSameAgenda() {
        Agenda agenda = saveAgenda();
        saveSession(agenda);

        //Creating another session should throw database integrity error
        assertThatThrownBy(() -> saveSession(agenda))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void rejectsSecondVoteFromTheSameMemberInTheSameSession() {
        Member member = saveMember();
        VotingSession session = saveSession(saveAgenda());
        saveVote(session, member, VoteChoice.YES);

        //Same member cannot vote for the same session
        assertThatThrownBy(() -> saveVote(session, member, VoteChoice.NO))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void rejectsDuplicatedDocument() {
        saveMember();

        assertThatThrownBy(this::saveMember)
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Member saveMember() {
        return memberRepository.saveAndFlush(Member.builder()
                .name("Member " + UUID.randomUUID())
                .document("12345678901")
                .type(MemberType.ASSOCIATE)
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private Agenda saveAgenda() {
        return agendaRepository.saveAndFlush(Agenda.builder()
                .description("Agenda description")
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private VotingSession saveSession(Agenda agenda) {
        OffsetDateTime now = OffsetDateTime.now();
        return votingSessionRepository.saveAndFlush(VotingSession.builder()
                .agenda(agenda)
                .openedAt(now)
                .closesAt(now.plusMinutes(1))
                .createdAt(now)
                .build());
    }

    private Vote saveVote(VotingSession session, Member member, VoteChoice choice) {
        return voteRepository.saveAndFlush(Vote.builder()
                .votingSession(session)
                .member(member)
                .choice(choice)
                .createdAt(OffsetDateTime.now())
                .build());
    }
}

package br.com.sicredi.vote.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "11144477735",
            "87748248800",
            "16899535009"
    })
    void acceptsValidCpf(String document) {
        assertThat(DocumentValidator.validateDocument(document)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "99999999999",
            "5299822472",
            "529982247250",
            "529.982.247-25",
            "5299822472a",
            "abcdefghijk",
            "5299822472 "
    })
    void rejectsInvalidDocument(String document) {
        assertThat(DocumentValidator.validateDocument(document)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rejectsNullOrBlankDocument(String document) {
        assertThat(DocumentValidator.validateDocument(document)).isFalse();
    }
}

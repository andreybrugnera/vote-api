package br.com.sicredi.vote.validator;

import java.util.regex.Pattern;

import io.micrometer.common.util.StringUtils;

public class DocumentValidator {

    private static final Pattern VALID_PATTERN = Pattern.compile("\\d{11}");
    private static final int CPF_LENGTH = 11;
    private static final int CHECK_DIGITS = 2;

    private DocumentValidator() {
    }

    public static boolean validateDocument(String document) {
        return StringUtils.isNotEmpty(document)
                && VALID_PATTERN.matcher(document).matches()
                && hasDistinctDigits(document)
                && hasValidCheckDigits(document);
    }

    /**
     * Repeated sequences such as 00000000000 satisfy the check digit algorithm
     * but are not issued CPFs.
     */
    private static boolean hasDistinctDigits(String document) {
        return document.chars().distinct().count() > 1;
    }

    private static boolean hasValidCheckDigits(String document) {
        for (int digitPosition = CPF_LENGTH - CHECK_DIGITS; digitPosition < CPF_LENGTH; digitPosition++) {
            if (checkDigitAt(document, digitPosition) != digitAt(document, digitPosition)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Each check digit is the weighted sum of every preceding digit, where the
     * weights count down from the number of digits being summed plus one.
     */
    private static int checkDigitAt(String document, int digitPosition) {
        int sum = 0;
        int weight = digitPosition + 1;

        for (int i = 0; i < digitPosition; i++) {
            sum += digitAt(document, i) * weight--;
        }

        int remainder = sum % CPF_LENGTH;
        return remainder < CHECK_DIGITS ? 0 : CPF_LENGTH - remainder;
    }

    private static int digitAt(String document, int index) {
        return Character.digit(document.charAt(index), 10);
    }
}

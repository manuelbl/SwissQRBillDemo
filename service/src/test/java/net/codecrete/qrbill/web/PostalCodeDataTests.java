//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.PostalCodeData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the {@link PostalCodeData} class
 */
@DisplayName("Postal code lookup")
class PostalCodeDataTests {

    private static PostalCodeData postalCodeData;

    @BeforeAll
    static void setup() {
        postalCodeData = new PostalCodeData();
    }

    @Test
    void singleMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "8302");
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).satisfies(pc -> {
            assertThat(pc.code).isEqualTo("8302");
            assertThat(pc.town).isEqualTo("Kloten");
        });
    }

    @Test
    void zurichFullName() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "Zürich");
        assertThat(result).hasSize(20);

        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertThat(pc.town).isEqualTo("Zürich");
            assertThat(pc.code)
                    .isBetween("8000", "8099")
                    .isGreaterThanOrEqualTo(previousCode);
            previousCode = pc.code;
        }
    }

    @Test
    void zurichSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "Züri");
        assertThat(result).hasSize(20);

        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertThat(pc.town).isEqualTo("Zürich");
            assertThat(pc.code)
                    .isBetween("8000", "8099")
                    .isGreaterThanOrEqualTo(previousCode);
            previousCode = pc.code;
        }
    }

    @Test
    void dorfSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", " dorf");
        assertThat(result).hasSize(20);

        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            if (previousTown.startsWith("Dorf") && !pc.town.startsWith("Dorf"))
                previousTown = ""; // Reset between "Dorf...." towns and "...dorf..." towns

            assertThat(pc.code).isNotNull();
            assertThat(pc.town)
                    .containsIgnoringCase("dorf")
                    .isGreaterThanOrEqualTo(previousTown);

            previousTown = pc.town;
        }
    }

    @Test
    void numericSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "203");
        assertThat(result).hasSize(12);

        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            if (previousCode.startsWith("203") && !pc.code.startsWith("203"))
                previousCode = "";

            assertThat(pc.code)
                    .contains("203")
                    .isGreaterThanOrEqualTo(previousCode);

            previousCode = pc.code;
        }
    }

    @Test
    void startsWith880() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("", "880 ");
        assertThat(result).hasSize(8);

        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertThat(pc.town).isNotNull();
            assertThat(pc.code)
                    .startsWith("880")
                    .isGreaterThanOrEqualTo(previousCode);
            previousCode = pc.code;
        }
    }

    @Test
    void startsWithRickenbach() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes(null, " Rickenbach");
        assertThat(result).hasSize(7);

        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertThat(pc.code).isNotNull();
            assertThat(pc.town)
                    .startsWith("Rickenbach")
                    .usingComparator(String.CASE_INSENSITIVE_ORDER)
                    .isGreaterThanOrEqualTo(previousTown);
            previousTown = pc.town;
        }
    }

    @Test
    void noMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "abc");
        assertThat(result).hasSize(0);
    }

    @Test
    void noMatchNumeric() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "0123");
        assertThat(result).hasSize(0);
    }

    @Test
    void unsupportedCountry() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("DE", "12");
        assertThat(result).hasSize(0);
    }
}

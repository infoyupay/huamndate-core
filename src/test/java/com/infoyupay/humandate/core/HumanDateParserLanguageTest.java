/*
 * humandate-core
 * COPYLEFT 2025
 * Ingenieria Informatica Yupay SACS
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 *  with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.infoyupay.humandate.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for localized features of {@link HumanDateParser} using
 * {@link Languages#en()}, {@link Languages#es()} and {@link Languages#que()}.
 * <br>
 * Validates today/tomorrow/yesterday keywords and relative offsets with
 * language-specific unit letters.
 *
 * <ul>
 *   <li>today, tomorrow, yesterday</li>
 *   <li>+1d, +1w, +1m, +1y with English unit letters</li>
 *   <li>+1d, +1s, +1m, +1a with Spanish unit letters</li>
 *   <li>+1p, +1h, +1k, +1w with Quechua unit letters</li>
 * </ul>
 * <div style="border: 1px solid black; padding: 2px">
 *    <strong>Execution Notes:</strong> dvidal@infoyupay.com passed 6 tests in 0.145s at 2025-12-08 23:15 UTC-5.
 * </div>
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
class HumanDateParserLanguageTest {

    /**
     * Validates English keywords and unit letters.
     */
    @Test
    void shouldParseEnglishKeywordsAndUnits() {
        var parser = new HumanDateParser().setLanguage(Languages.en());
        var now = LocalDate.now();

        // Keywords
        assertThat(parser.apply("today")).isEqualTo(now);
        assertThat(parser.apply("now")).isEqualTo(now);
        assertThat(parser.apply("yesterday")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("ytd")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("tomorrow")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("tmr")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("tmw")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("tmrw")).isEqualTo(now.plusDays(1));

        // Relative with units
        assertThat(parser.apply("+1d")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("+1w")).isEqualTo(now.plusWeeks(1));
        assertThat(parser.apply("+1m")).isEqualTo(now.plusMonths(1));
        assertThat(parser.apply("+1y")).isEqualTo(now.plusYears(1));
    }

    /**
     * Validates Spanish keywords and unit letters.
     */
    @Test
    void shouldParseSpanishKeywordsAndUnits() {
        var parser = new HumanDateParser().setLanguage(Languages.es());
        var now = LocalDate.now();

        // Keywords
        assertThat(parser.apply("hoy")).isEqualTo(now);
        assertThat(parser.apply("ya")).isEqualTo(now);
        assertThat(parser.apply("ahora")).isEqualTo(now);
        assertThat(parser.apply("ayer")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("mañana")).isEqualTo(now.plusDays(1));

        // Relative with units (d,s,m,a)
        assertThat(parser.apply("+1d")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("+1s")).isEqualTo(now.plusWeeks(1));
        assertThat(parser.apply("+1m")).isEqualTo(now.plusMonths(1));
        assertThat(parser.apply("+1a")).isEqualTo(now.plusYears(1));
    }

    /**
     * Validates Quechua keywords and unit letters.
     */
    @Test
    void shouldParseQuechuaKeywordsAndUnits() {
        var parser = new HumanDateParser().setLanguage(Languages.que());
        var now = LocalDate.now();

        // Keywords
        assertThat(parser.apply("kunan")).isEqualTo(now);
        assertThat(parser.apply("kaypi")).isEqualTo(now);
        assertThat(parser.apply("ña")).isEqualTo(now);
        assertThat(parser.apply("qayna")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("jainapunchau")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("qaynunchay")).isEqualTo(now.minusDays(1));
        assertThat(parser.apply("paqarin")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("qaya")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("haya")).isEqualTo(now.plusDays(1));

        // Relative with units (p,h,k,w)
        assertThat(parser.apply("+1p")).isEqualTo(now.plusDays(1));
        assertThat(parser.apply("+1h")).isEqualTo(now.plusWeeks(1));
        assertThat(parser.apply("+1k")).isEqualTo(now.plusMonths(1));
        assertThat(parser.apply("+1w")).isEqualTo(now.plusYears(1));
    }

    /**
     * Ensures that strings do not match any localized keyword or
     * relative pattern in English, returning {@code null}.<br>
     * This verifies rejection paths are language–aware.
     */
    @Test
    void shouldReturnNull_whenNoPatternMatches_English() {
        var parser = new HumanDateParser().setLanguage(Languages.en());

        assertThat(parser.apply("tod")).isNull();
        assertThat(parser.apply("+1x")).isNull();
        assertThat(parser.apply("unknown")).isNull();
    }

    /**
     * Ensures that Spanish parser rejects invalid inputs cleanly
     * and returns {@code null}.
     */
    @Test
    void shouldReturnNull_whenNoPatternMatches_Spanish() {
        var parser = new HumanDateParser().setLanguage(Languages.es());

        assertThat(parser.apply("manan")).isNull(); // typo
        assertThat(parser.apply("+1j")).isNull(); // unsupported unit
        assertThat(parser.apply("otro")).isNull();
    }

    /**
     * Ensures that Quechua parser does not fallback to other
     * languages nor numeric shortcuts when not explicitly matched.
     */
    @Test
    void shouldReturnNull_whenNoPatternMatches_Quechua() {
        var parser = new HumanDateParser().setLanguage(Languages.que());

        assertThat(parser.apply("kunnan")).isNull(); // typo
        assertThat(parser.apply("+1x")).isNull();
        assertThat(parser.apply("qaynaaa")).isNull();
    }

}

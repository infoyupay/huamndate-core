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
 * Tests for numeric parsing capabilities of {@link HumanDateParser}.
 * <br>
 * This suite validates compact formats and separated formats, as well as
 * relative day offsets without language configuration.
 *
 * <ul>
 *   <li>dd → current month/year</li>
 *   <li>ddMM → current year</li>
 *   <li>ddMMyy / ddMMyyyy</li>
 *   <li>d.M.yy | d/M/yy | d-M-yy | d·M·yy and idem with four-digit year</li>
 *   <li>+d / -d relative offsets</li>
 * </ul>
 * <div style="border: 1px solid black; padding: 2px">
 *    <strong>Execution Notes:</strong> dvidal@infoyupay.com passed 8 tests in 0.174s at 2025-12-08 23:15 UTC-5.
 * </div>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
class HumanDateParserNumericTest {

    /**
     * Verifies that a standalone day is interpreted within the current month/year
     * when it fits the month length.
     */
    @Test
    void shouldParseStandaloneDay_dd() {
        // Given
        var parser = new HumanDateParser();
        var now = LocalDate.now();

        // When
        var parsed = parser.apply("15");

        // Then
        assertThat(parsed).isEqualTo(now.withDayOfMonth(15));
    }

    /**
     * Ensures that ddMM compact form maps to current year.
     */
    @Test
    void shouldParse_ddMM() {
        var parser = new HumanDateParser();
        var now = LocalDate.now();

        var parsed = parser.apply("1001");

        assertThat(parsed).isEqualTo(now.withMonth(1).withDayOfMonth(10));
    }

    /**
     * Ensures that ddMMyy compact form applies the 2000s pivot for 2-digit years.
     */
    @Test
    void shouldParse_ddMMyy() {
        var parser = new HumanDateParser();

        var parsed = parser.apply("120424");

        assertThat(parsed).isEqualTo(LocalDate.of(2024, 4, 12));
    }

    /**
     * Ensures that ddMMyyyy compact form preserves the explicit year.
     */
    @Test
    void shouldParse_ddMMyyyy() {
        var parser = new HumanDateParser();

        var parsed = parser.apply("12032025");

        assertThat(parsed).isEqualTo(LocalDate.of(2025, 3, 12));
    }

    /**
     * Verifies day offsets without unit letters.
     */
    @Test
    void shouldParse_dayOffsets_withoutLanguage() {
        var parser = new HumanDateParser();
        var now = LocalDate.now();

        assertThat(parser.apply("+3")).isEqualTo(now.plusDays(3));
        assertThat(parser.apply("-5")).isEqualTo(now.minusDays(5));
    }

    /**
     * Verifies day-month-year with different separators and a two-digit year.
     */
    @Test
    void shouldParse_dSepMSepYY_forAllSeparators() {
        var parser = new HumanDateParser();
        var expected = LocalDate.of(2025, 2, 1);

        assertThat(parser.apply("1.2.25")).isEqualTo(expected);
        assertThat(parser.apply("1/2/25")).isEqualTo(expected);
        assertThat(parser.apply("1-2-25")).isEqualTo(expected);
        assertThat(parser.apply("1·2·25")).isEqualTo(expected);
    }

    /**
     * Verifies day-month-year with different separators and a four-digit year.
     */
    @Test
    void shouldParse_dSepMSepYYYY_forAllSeparators() {
        var parser = new HumanDateParser();
        var expected = LocalDate.of(2025, 2, 1);

        assertThat(parser.apply("1.2.2025")).isEqualTo(expected);
        assertThat(parser.apply("1/2/2025")).isEqualTo(expected);
        assertThat(parser.apply("1-2-2025")).isEqualTo(expected);
        assertThat(parser.apply("1·2·2025")).isEqualTo(expected);
    }

    /**
     * Ensures that non–numeric content without language configuration
     * does not match any valid pattern and therefore returns {@code null}.<br>
     * Examples include:
     * <ul>
     *   <li>alphabetic only</li>
     *   <li>invalid month/day combination</li>
     *   <li>unsupported separators</li>
     * </ul>
     */
    @Test
    void shouldReturnNull_whenNoPatternMatches_withoutLanguage() {
        var parser = new HumanDateParser();

        assertThat(parser.apply("abc")).isNull();
        assertThat(parser.apply("32-13-25")).isNull();
        assertThat(parser.apply("1*2*2025")).isNull();
        assertThat(parser.apply("")).isNull();
        assertThat(parser.apply(" ")).isNull();
    }

}

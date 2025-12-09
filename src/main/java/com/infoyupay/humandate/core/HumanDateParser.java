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

import java.time.LocalDate;
import java.util.function.Function;

import static com.infoyupay.humandate.core.ParserUtils.*;

/**
 * A human-friendly date parser that recognizes lightweight and intuitive
 * textual representations of calendar dates.
 * <br>
 * HumanDateParser provides ergonomic date input by allowing fast keyboard-friendly
 * formats and localized shortcuts for "today" and relative date arithmetic.
 * <p>
 * For expressions using relative offsets, such as {@code +2d} or {@code -1m},
 * this parser relies on the configured {@link LanguageSupport} instances to supply the
 * unit letters that correspond to calendar-based time units:
 * days, weeks, months and years.
 * </p>
 * <p>
 * This parser focuses on pragmatic patterns commonly used in everyday typing,
 * without requiring strict ISO formats or full localization. It delegates all
 * conversion logic to {@link ParserUtils}, and only decides which parsing rule
 * to apply based on regular expression detection.
 * <p>
 * Supported categories include:
 * <ul>
 *   <li>Standalone day values (e.g., "7", "31")</li>
 *   <li>Day-month with a separator (e.g., "7/12", "01-1")</li>
 *   <li>Day-month-year with a separator (e.g., "7/12/25", "7-12-2025")</li>
 *   <li>Compact formats without separators (ddMM, ddMMyy/yyyy)</li>
 *   <li>Relative offsets: +N / -N days (e.g., "+3", "-10")</li>
 *   <li>Keywords referring to today (e.g., "hoy", "today", "now", "ahora", "ya")</li>
 * </ul>
 * <p>
 * Year interpretation uses a small pivot rule:
 * if a year is provided with only 1–2 digits, it is assumed to be 20xx.
 * Four-digit years are preserved.
 * <p>
 * If the input does not match any known pattern, {@code null} is returned.
 * A richer error model may be introduced in future versions.
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 * @see ParserUtils
 * @see java.time.LocalDate
 */
public class HumanDateParser implements Function<String, LocalDate> {
    @Override
    public LocalDate apply(String string) {
        // Normalize input: treat null as empty and remove surrounding whitespace
        var _string = string == null ? "" : string.strip();

        /*
         * === “Today” keywords ===
         * Matches literal keywords referring to the current date.
         * Word boundaries ensure we don't accidentally match fragments
         * inside other words (e.g., "playa").
         *
         * Examples: "hoy", "today", "now", "ahora", "ya", "0", "+0", "-0"
         *Today keywords: allow whitespace and punctuation around token
         */
        if (_string.matches("(?i)^\\s*\\p{Punct}*(hoy|today|now|ahora|ya)\\p{Punct}*\\s*$")
                || _string.matches("0|-0|\\+0")) {
            return LocalDate.now();
        }


        /*
         * === Day offset (positive) ===
         * Example: "+7" → current date plus 7 days
         */
        if (_string.matches("\\+\\d+")) {
            return parsePlusD(_string);
        }

        /*
         * === Day offset (negative) ===
         * Example: "-3" → current date minus 3 days
         */
        if (_string.matches("-\\d+")) {
            return parseMinusD(_string);
        }

        /*
         * === Day only ===
         * Interpreted later depending on context (day of month vs day since epoch)
         * Valid values: 1–31 (leading zero optional)
         *
         * Examples: "7", "07", "31"
         */
        if (_string.matches("[1-9]|[12]\\d|3[01]|0[1-9]")) {
            return parseDD(_string);
        }

        /*
         * === Day-Month with separator ===
         * Format: d/M where separator is one of: "-", "/", ".", "·"
         *
         * Examples: "7/12", "01-1", "3.09", "7·8"
         */
        if (_string.matches("([1-9]|[12]\\d|3[01])([-/.·])([1-9]|1[0-2])")) {
            return parseDD_MM(_string);
        }

        /*
         * === Day-Month-Year with separator ===
         * Format: d/M/y (year: 1 to 4 digits)
         * Year semantics resolved later (e.g., pivot for 1–2 digits → 20xx)
         *
         * Examples: "7/12/25", "7-12-2025", "01.1.5"
         */
        if (_string.matches("([1-9]|[12]\\d|3[01])([-/.·])([1-9]|1[0-2])([-/.·])(\\d{1,4})")) {
            return parseDD_MM_YY(_string);
        }

        /*
         * === DayMonth without separator ===
         * Exactly 4 digits → avoids ambiguity
         *
         * Examples: "0712", "3112"
         * (NOT valid: "712" or "112")
         */
        if (_string.matches("(0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2])")) {
            return parseDDMM(_string);
        }

        /*
         * === DayMonthYear without separator ===
         * Year: exactly 2 or 4 digits
         * Examples: "071225", "07122025"
         */
        if (_string.matches("(0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2])(\\d{4}|\\d{2})")) {
            return parseDDMMYY(_string);
        }

        /*
         * Anything else → not recognized as a HumanDate pattern
         */
        return null;
    }
}

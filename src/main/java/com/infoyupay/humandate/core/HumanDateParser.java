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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.infoyupay.humandate.core.ParserUtils.*;

/**
 * A lightweight and ergonomic parser for human-typed calendar dates.
 * <p>
 * HumanDateParser focuses on fast numeric input that does not interrupt
 * the user's cognitive flow, while optionally supporting localized shortcuts
 * for <em>today</em>, <em>yesterday</em> and <em>tomorrow</em>, and
 * localized unit identifiers for relative date arithmetic.
 * </p>
 *
 * <h2>Language configuration</h2>
 * <p>
 * The parser operates in two modes:
 * </p>
 * <ul>
 *   <li><strong>Numeric-only mode</strong> (default): only numeric formats
 *       and day-based offsets ({@code +N}, {@code -N}) are recognized.</li>
 *   <li><strong>Localized mode</strong>: enabled by calling
 *       {@link #setLanguage(LanguageSupport)}, which activates recognition of
 *       language-specific keywords (e.g. {@code hoy}, {@code today},
 *       {@code kunan}) and unit letters for
 *       {@link java.time.temporal.ChronoUnit#DAYS DAYS},
 *       {@link java.time.temporal.ChronoUnit#WEEKS WEEKS},
 *       {@link java.time.temporal.ChronoUnit#MONTHS MONTHS},
 *       and {@link java.time.temporal.ChronoUnit#YEARS YEARS}.
 * </li>
 * </ul>
 * <p>
 * If a unit letter is unrecognized in the current language, the parser defaults
 * safely to day-based arithmetic to preserve ergonomics and user intent.
 * </p>
 *
 * <h2>Supported categories</h2>
 * <ul>
 *   <li>Standalone day values (e.g. {@code "7"}, {@code "31"})</li>
 *   <li>Day-month with a separator (e.g. {@code "7/12"}, {@code "01-1"})</li>
 *   <li>Day-month-year with a separator (e.g. {@code "7/12/25"},
 *       {@code "7-12-2025"})</li>
 *   <li>Compact formats without separators (<em>ddMM</em>,
 *       <em>ddMMyy</em>/<em>ddMMyyyy</em>)</li>
 *   <li>Relative offsets: {@code +N}/{@code -N} days, optionally with units:
 *       {@code +3d}, {@code -2w}, {@code +1m}, {@code -5y}</li>
 *   <li>Localized keywords referring to today / yesterday / tomorrow,
 *       depending on the configured {@link LanguageSupport}</li>
 * </ul>
 *
 * <h2>Year interpretation</h2>
 * <p>
 * When a year is provided with only 1–2 digits, the parser assumes a value in
 * the 2000s (example: {@code “25” → 2025}).
 * Four-digit years are preserved as-is.
 * </p>
 *
 * <p>
 * Conversion logic is delegated to {@link ParserUtils};
 * this class only determines which parsing rule to apply.
 * </p>
 *
 * <p>
 * If the input does not match any known pattern, {@code null} is returned.
 * A richer error model may be introduced in future versions.
 * </p>
 *
 * @author David Vidal, InfoYupay
 * @version 1.0
 * @see LanguageSupport
 * @see ParserUtils
 * @see java.time.LocalDate
 */
public final class HumanDateParser implements Function<String, LocalDate> {

    /**
     * Pattern for relative-date expressions that include an explicit unit letter,
     * such as {@code +2d}, {@code -3w}, {@code +1m}.
     * <p>
     * Structure:
     * <ul>
     *   <li>Leading sign: '+' or '-'</li>
     *   <li>Integer value: at least one digit</li>
     *   <li>Single trailing unit letter: [a-zA-Z]</li>
     * </ul>
     * <p>
     * Unit semantics are resolved through {@link LanguageSupport#unitLetters()}.
     * If the unit letter is not recognized for the active language,
     * <strong>day-based arithmetic is used as a fallback</strong>.
     */
    private static final Pattern OFFSET_WITH_UNIT =
            Pattern.compile("^[+-]\\d+[a-zA-Z]$");

    /**
     * The currently active language support module.
     * <p>
     * If this value is {@code null}, the parser operates in
     * <strong>numeric-only mode</strong> and only recognizes purely numeric
     * formats (e.g. {@code dd}, {@code dd/MM}, {@code +7}).
     * <p>
     * When a language is set via {@link #setLanguage(LanguageSupport)},
     * localized keywords and unit letters become available.
     */
    private LanguageSupport language; // nullable = numeric-only mode

    /*===============================*
     * Computed from LanguageSupport *
     *===============================*/

    /**
     * Keyword-based pattern recognizing expressions that mean "today"
     * in the active language, such as {@code hoy}, {@code today},
     * {@code kunan}, optionally surrounded by whitespace or punctuation.
     * <p>
     * <strong>Null when the active language does not define today keywords.</strong>
     */
    private Pattern todayPattern;
    /**
     * Keyword-based pattern recognizing expressions meaning "yesterday"
     * (e.g. {@code ayer}, {@code yesterday}, {@code qayna})
     * optionally surrounded by punctuation.
     * <p>
     * <strong>Null when the active language does not define yesterday keywords.</strong>
     */
    private Pattern yesterdayPattern;
    /**
     * Keyword-based pattern for expressions meaning "tomorrow"
     * (e.g. {@code mañana}, {@code tomorrow}, {@code paqarin}).
     * <p>
     * <strong>Null when the active language does not define tomorrow keywords.</strong>
     */
    private Pattern tomorrowPattern;

    /*==============================*
     * One-letter unit → ChronoUnit *
     *==============================*/

    /**
     * Keyword-based pattern for expressions meaning "tomorrow"
     * (e.g. {@code mañana}, {@code tomorrow}, {@code paqarin}).
     * <p>
     * <strong>Null when the active language does not define tomorrow keywords.</strong>
     */
    private Map<String, ChronoUnit> resolvedUnits = Map.of();

    /**
     * Activates localized parsing by configuring a language support module.
     * <p>
     * Patterns and unit mappings are computed immediately upon assignment.
     * Passing {@code null} disables all localization features and restores
     * numeric-only parsing.
     *
     * @param languageSupport the language module to apply.
     * @return this parser instance for fluent configuration.
     */
    public HumanDateParser setLanguage(LanguageSupport languageSupport) {
        this.language = languageSupport;
        // here patterns are computed and units mapped.
        computeLanguageRules();
        return this;
    }

    /**
     * Builds keyword-based recognition rules for the active language
     * and resolves single-letter unit codes into date-based {@link ChronoUnit} values.
     * <p>
     * Called automatically by {@link #setLanguage(LanguageSupport)}.
     * <p>
     * If no language is active, all recognition patterns are reset to {@code null}
     * and the unit-resolution map becomes empty.
     */
    private void computeLanguageRules() {
        // Build shortcut patterns from keywords
        todayPattern = buildKeywordPattern(language.todayKeywords());
        yesterdayPattern = buildKeywordPattern(language.yesterdayKeywords());
        tomorrowPattern = buildKeywordPattern(language.tomorrowKeywords());

        // Build unit letter resolution table
        resolvedUnits = language.unitLetters().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toLowerCase(),      // normalize
                        Map.Entry::getValue,
                        (existing, conflict) -> existing    // existing wins over composite hacks
                ));
    }

    /**
     * Builds a pattern that recognizes any of the provided keywords
     * as a full input value meaning "today", "yesterday", or "tomorrow",
     * depending on the semantic source.
     * <p>
     * Matching is case-insensitive and allows surrounding whitespace and punctuation,
     * but does not allow embedding the keyword within another word.
     * <p>
     * If the keyword list is empty or {@code null}, {@code null} is returned,
     * indicating that this semantic case is unsupported by the active language.
     *
     * @param keywords the list of raw language keywords to normalize and wrap.
     * @return a fully compiled keyword pattern, or {@code null}.
     */
    @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
    private Pattern buildKeywordPattern(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null; // no support
        }

        // Escape all words to avoid regex breakers
        var union = keywords.stream()
                .map(String::toLowerCase)
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        // Example: "^(?i)\s*[\p{Punct}]*(hoy|ya)[\p{Punct}]*\s*$"
        return Pattern.compile("(?i)^\\s*\\p{Punct}*(?:"
                + union +
                ")\\p{Punct}*\\s*$");
    }

    @Override
    public LocalDate apply(String string) {
        // Normalize input: treat null as empty and remove surrounding whitespace
        var _string = string == null ? "" : string.strip();

        // -----------------------------------------------
        // Localized date keywords ("hoy", "today", "kunan")
        // -----------------------------------------------
        if (language != null) {
            if (todayPattern != null && todayPattern.matcher(_string).matches()) {
                return LocalDate.now();
            }
            if (yesterdayPattern != null && yesterdayPattern.matcher(_string).matches()) {
                return LocalDate.now().minusDays(1);
            }
            if (tomorrowPattern != null && tomorrowPattern.matcher(_string).matches()) {
                return LocalDate.now().plusDays(1);
            }

            // -----------------------------------------------
            // Relative offsets with units (+2d, -1m, etc.)
            // -----------------------------------------------
            if (OFFSET_WITH_UNIT.matcher(_string).matches()) {
                return parseOffsetWithUnit(_string, resolvedUnits);
            }
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

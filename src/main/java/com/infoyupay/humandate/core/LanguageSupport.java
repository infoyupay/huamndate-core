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

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Provides localized keywords and unit identifiers that enable
 * human-friendly date parsing in {@link HumanDateParser}.
 * <p>
 * Implementations may supply keywords for concepts like
 * "today", "yesterday" or "tomorrow", allowing commands such as:
 * {@code hoy}, {@code today}, {@code jetzt}, etc.
 * </p>
 *
 * <p>
 * Implementations may also provide one-letter unit symbols to perform
 * relative date arithmetic such as:
 * {@code +2d}, {@code -3w}, {@code +1m} or {@code -5y}.
 * These units should map to calendar-based {@link ChronoUnit} values.
 * </p>
 *
 * <h2>Required supported units</h2>
 * <p>
 * To ensure consistent behavior across all languages, implementations
 * are encouraged to provide unit letters (case-insensitive) for the
 * following date-based {@link ChronoUnit} values:
 * </p>
 *
 * <ul>
 *   <li>{@link ChronoUnit#DAYS}   – day offsets (e.g. {@code +3d})</li>
 *   <li>{@link ChronoUnit#WEEKS}  – week offsets (e.g. {@code -2w})</li>
 *   <li>{@link ChronoUnit#MONTHS} – month offsets (e.g. {@code +1m})</li>
 *   <li>{@link ChronoUnit#YEARS}  – year offsets (e.g. {@code -5y})</li>
 * </ul>
 *
 * <p>
 * Units that do not convert directly to calendar dates (such as hours
 * or minutes) are intentionally not supported, because the parser
 * operates strictly on {@link java.time.LocalDate}.
 * </p>
 *
 * <h2>Ergonomic defaults</h2>
 * <p>
 * If a relative expression does not include a unit letter
 * (e.g. {@code +7} or {@code -10}), the parser defaults to
 * day-based arithmetic for maximum typing ergonomics.
 * </p>
 *
 * <p>
 * A HumanDateParser instance is typically configured with a single
 * LanguageSupport at a time. If you need multi-language behavior,
 * consider implementing a composite LanguageSupport that merges
 * several languages explicitly and resolves any conflicts.
 * </p>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public interface LanguageSupport {

    /**
     * Case-insensitive matching is applied by the parser.
     *
     * @return the localized keywords meaning "today".
     */
    List<String> todayKeywords();

    /**
     * Default: no support.
     *
     * @return localized keywords meaning "yesterday".
     */
    default List<String> yesterdayKeywords() {
        return List.of();
    }

    /**
     * Default: no support.
     *
     * @return localized keywords meaning "tomorrow".
     */
    default List<String> tomorrowKeywords() {
        return List.of();
    }

    /**
     * Returns a mapping of unit letters to calendar-based
     * {@link ChronoUnit} values for relative date arithmetic.
     * <p>
     * Each key must be a single-character string (case-insensitive),
     * representing the unit used in expressions such as {@code +2d}
     * or {@code -1w}.
     * </p>
     *
     * @return a map from unit letter to date-based ChronoUnit.
     */
    Map<String, ChronoUnit> unitLetters();
}

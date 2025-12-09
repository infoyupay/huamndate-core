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
import java.util.Map;

/**
 * Utility class providing low-level parsing logic for HumanDate formats.
 * All methods assume that textual validation/regex detection has already been performed.
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public final class ParserUtils {

    /**
     * Private constructor to prevent instantiation
     * of a utility class.
     */
    private ParserUtils() {
        // Utility class: no instances
    }

    /**
     * Parses a standalone day value.
     * <p>
     * Rules:
     * - If the day fits inside the current month boundaries,
     * returns that day in the current month/year.
     * - Otherwise, interprets the number as "day since epoch"
     * via {@link LocalDate#ofEpochDay(long)}.
     * <p>
     * Example:
     * "7"  -> currentYear-currentMonth-07
     * "45" -> 1970-02-14 (epochDay 45)
     *
     * @param string numeric day value
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseDD(String string) {
        final var day = Long.parseLong(string);
        final var now = LocalDate.now();

        if (day > 0 && day <= now.lengthOfMonth()) {
            return now.withDayOfMonth((int) day);
        }
        return LocalDate.ofEpochDay(day);
    }

    /**
     * Parses a date in the form d/M with a dynamic separator
     * (any of '-', '/', '.', '·').
     * Day and month normalization relies on LocalDate rules.
     * <p>
     * Examples:
     * "7/12" -> currentYear-12-07
     * "01-1" -> currentYear-01-01
     *
     * @param string date in "d⟨sep⟩M" format
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseDD_MM(String string) {
        final var parts = string.split("[-/.·]");
        final var day = Integer.parseInt(parts[0]);
        final var month = Integer.parseInt(parts[1]);

        return LocalDate.now()
                .withDayOfMonth(1)
                .withMonth(month)
                .plusDays(day - 1);
    }

    /**
     * Parses a date in the form d/M/y where year may contain:
     * - 1 digit  ("5"   -> 2005)
     * - 2 digits ("25"  -> 2025)
     * - 4 digits ("2025")
     * <p>
     * 3-digit years are currently unsupported by design.
     * <p>
     * Pivot rule:
     * - If year < 100 → assume 20xx
     * <p>
     * Examples:
     * "7/12/25"   -> 2025-12-07
     * "7-12-2025" -> 2025-12-07
     *
     * @param string date in "d⟨sep⟩M⟨sep⟩y" format
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseDD_MM_YY(String string) {
        final var parts = string.split("[-/.·]");
        final var day = Integer.parseInt(parts[0]);
        final var month = Integer.parseInt(parts[1]);

        var year = Integer.parseInt(parts[2]);
        if (year >= 0 && year < 100) {
            year += 2000;
        }

        return LocalDate.of(year, month, 1)
                .plusDays(day - 1);
    }

    /**
     * Parses a date in compact form ddMM (exactly 4 digits).
     * This format intentionally avoids ambiguity.
     * <p>
     * Example:
     * "0712" -> currentYear-12-07
     *
     * @param string date in "ddMM" format
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseDDMM(String string) {
        final var day = Integer.parseInt(string.substring(0, 2));
        final var month = Integer.parseInt(string.substring(2, 4));

        return LocalDate.now()
                .withDayOfMonth(1)
                .withMonth(month)
                .plusDays(day - 1);
    }

    /**
     * Parses compact ddmmyy or ddmmyyyy forms (6 or 8 digits).
     * <p>
     * Pivot rule:
     * - Only if year has 2 digits → assume 20xx
     * <p>
     * Examples:
     * "071225"   -> 2025-12-07
     * "07122025" -> 2025-12-07
     *
     * @param string date in "ddmmyy" or "ddmmyyyy"
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseDDMMYY(String string) {
        final var day = Integer.parseInt(string.substring(0, 2));
        final var month = Integer.parseInt(string.substring(2, 4));

        var yearPart = string.substring(4);
        var year = Integer.parseInt(yearPart);

        if (yearPart.length() == 2 && year < 100) {
            year += 2000;
        }

        return LocalDate.of(year, month, 1)
                .plusDays(day - 1);
    }

    /**
     * Parses a date by adding a positive day offset to today.
     * <p>
     * Example:
     * "+3" -> today + 3 days
     *
     * @param string day offset prefixed with '+'
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parsePlusD(String string) {
        return LocalDate.now().plusDays(Integer.parseInt(string.substring(1)));
    }

    /**
     * Parses a date by subtracting a negative day offset from today.
     * <p>
     * Example:
     * "-2" -> today - 2 days
     *
     * @param string day offset prefixed with '-'
     * @return resolved {@link LocalDate}
     */
    public static LocalDate parseMinusD(String string) {
        return LocalDate.now().minusDays(Integer.parseInt(string.substring(1)));
    }

    /**
     * Parses a relative date expression using a localized time unit.
     * <p>
     * This method interprets strings of the form:
     * {@code +Nd}, {@code -2w}, {@code +1m}, {@code -3y},
     * where the leading sign ({@code +/-}) indicates direction and the
     * trailing letter indicates the calendar unit.
     * </p>
     *
     * <h4>Unit resolution</h4>
     * <p>
     * The provided {@code resolvedUnits} specifies how unit letters are mapped to
     * {@link java.time.temporal.ChronoUnit}. If the letter is not present in
     * the map, the parser falls back safely to {@link ChronoUnit#DAYS}.
     * This ensures ergonomic behavior even when the user types an unknown or
     * mistyped unit.
     * </p>
     *
     * <h4>Base date reference</h4>
     * <p>
     * All offsets are calculated relative to {@link LocalDate#now()}.
     * </p>
     *
     * <h4>Examples</h4>
     * <ul>
     *     <li>{@code +3d} → 3 days from today</li>
     *     <li>{@code -1w} → 1 week before today</li>
     *     <li>{@code +2m} → 2 months from today</li>
     *     <li>{@code -5y} → 5 years before today</li>
     *     <li>{@code +10x} → 10 days from today (fallback)</li>
     * </ul>
     *
     * @param string        the relative expression, guaranteed by the caller
     *                      to match the format {@code ^[+-]\d+[a-zA-Z]$}
     * @param resolvedUnits a unit-resolution map provided by
     *                      {@link com.infoyupay.humandate.core.LanguageSupport}
     * @return a computed {@link LocalDate} relative to now
     */
    public static LocalDate parseOffsetWithUnit(String string, Map<String, ChronoUnit> resolvedUnits) {
        var base = LocalDate.now();

        var sign = string.charAt(0);
        var number = string.substring(1, string.length() - 1);
        var unitLetter = string.substring(string.length() - 1).toLowerCase();

        var unit = resolvedUnits.getOrDefault(unitLetter, ChronoUnit.DAYS);

        var amount = Long.parseLong(number);

        return (sign == '+')
                ? base.plus(amount, unit)
                : base.minus(amount, unit);
    }
}

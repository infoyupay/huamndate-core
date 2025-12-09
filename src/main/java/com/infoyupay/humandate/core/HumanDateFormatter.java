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
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A minimal LocalDate → String formatter with safe defaults.
 * <p>
 * By default, this formatter renders dates using the human-friendly
 * pattern {@code dd/MM/yyyy}. A custom {@link DateTimeFormatter} can be
 * injected to override the default without replacing the formatter
 * instance.
 *
 * <p>
 * This formatter is intentionally simple: no locale inference,
 * no ambiguous shortcuts, no relative formatting — just clean
 * calendar dates ready for UI usage.
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public final class HumanDateFormatter implements Function<LocalDate, String> {

    private DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Overrides the default formatting rule.
     *
     * @param formatter a formatter for rendering date values
     * @return this instance for fluent chaining
     */
    public HumanDateFormatter withFormatter(DateTimeFormatter formatter) {
        if (formatter != null) {
            this.formatter = formatter;
        }
        return this;
    }

    /**
     * Restores the default ergonomic formatting rule {@code dd/MM/yyyy}.
     * <p>
     * This is the explicit reset counterpart to {@link #withFormatter(DateTimeFormatter)},
     * ensuring that callers do not unintentionally override their own configuration.
     * Providing a separate method for this behavior keeps the API expressive and
     * prevents unexpected changes when {@code null} is passed to the formatter setter.
     * </p>
     *
     * @return this instance for fluent chaining
     * @see #withFormatter(DateTimeFormatter)
     * @since 1.0
     */
    public HumanDateFormatter withDefaultFormatter() {
        return withFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String apply(LocalDate date) {
        return (date == null) ? null : formatter.format(date);
    }
}


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

import com.infoyupay.humandate.core.lang.LanguageSupportEN;
import com.infoyupay.humandate.core.lang.LanguageSupportES;
import com.infoyupay.humandate.core.lang.LanguageSupportQUE;

/**
 * Convenience factory for built-in {@link LanguageSupport} implementations.
 * <p>
 * This class exposes stable entry points to built-in language modules without
 * requiring consumers to reference internal implementation classes directly.
 * All returned instances are stateless and safe to reuse.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * HumanDateParser parser = new HumanDateParser()
 *         .setLanguage(Languages.es());
 * }</pre>
 *
 * <p>
 * The existence and behavior of these language methods forms part of the
 * public API contract. However, the specific classes behind them do not:
 * future versions may replace or extend them without impacting callers.
 * </p>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public final class Languages {
    /**
     * Prevents utility class instanciation.
     */
    private Languages() {
        // utility class, not meant to be instantiated
    }

    /**
     * Creates a new English language support instance.
     *
     * @return a {@link LanguageSupport} configured for English keywords
     * and time units
     */
    public static LanguageSupport en() {
        return new LanguageSupportEN();
    }

    /**
     * Creates a new Spanish language support instance.
     *
     * @return a {@link LanguageSupport} configured for Spanish keywords
     * and time units
     */
    public static LanguageSupport es() {
        return new LanguageSupportES();
    }

    /**
     * Creates a new Quechua language support instance.
     *
     * @return a {@link LanguageSupport} configured for Quechua keywords
     * and time units
     */
    public static LanguageSupport que() {
        return new LanguageSupportQUE();
    }
}


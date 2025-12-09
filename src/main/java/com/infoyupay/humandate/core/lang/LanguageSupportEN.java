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

package com.infoyupay.humandate.core.lang;

import com.infoyupay.humandate.core.LanguageSupport;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * English (EN) language support for HumanDate.
 * <p>
 * Contains keywords for "today", "yesterday" and "tomorrow",
 * including common informal abbreviations,
 * and unit letters compatible with English date terminology.
 * </p>
 *
 * <p><strong>Unit letters:</strong></p>
 * <ul>
 *   <li>{@code d} – days</li>
 *   <li>{@code w} – weeks</li>
 *   <li>{@code m} – months</li>
 *   <li>{@code y} – years</li>
 * </ul>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public class LanguageSupportEN implements LanguageSupport {
    @Override
    public List<String> todayKeywords() {
        return List.of("now", "today");
    }

    @Override
    public Map<String, ChronoUnit> unitLetters() {
        return Map.of(
                "d", ChronoUnit.DAYS,
                "w", ChronoUnit.WEEKS,
                "m", ChronoUnit.MONTHS,
                "y", ChronoUnit.YEARS
        );
    }

    @Override
    public List<String> yesterdayKeywords() {
        return List.of("yesterday", "ytd");
    }

    @Override
    public List<String> tomorrowKeywords() {
        return List.of("tomorrow", "tmr", "tmw", "tmrw");
    }
}

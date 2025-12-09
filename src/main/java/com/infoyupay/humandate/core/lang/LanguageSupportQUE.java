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
 * Quechua (QUE) language support for HumanDate.
 * <p>
 * Includes localized keywords for "today", "yesterday" and "tomorrow"
 * based on common modern Quechua dialect usage,
 * and culturally relevant unit letters derived from Quechua terminology.
 * </p>
 *
 * <p><strong>Unit letters:</strong></p>
 * <ul>
 *   <li>{@code p} – puncha (days)</li>
 *   <li>{@code h} – hunta (weeks)</li>
 *   <li>{@code k} – killa (months)</li>
 *   <li>{@code w} – wata (years)</li>
 * </ul>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
public class LanguageSupportQUE implements LanguageSupport {
    @Override
    public List<String> todayKeywords() {
        return List.of("kunan", "kaypi", "ña");
    }

    @Override
    public Map<String, ChronoUnit> unitLetters() {
        return Map.of(
                "p", ChronoUnit.DAYS, //(p)uncha
                "h", ChronoUnit.WEEKS, //(h)unkay
                "k", ChronoUnit.MONTHS,//(k)illa
                "w", ChronoUnit.YEARS//(w)ata
        );
    }

    @Override
    public List<String> tomorrowKeywords() {
        return List.of("paqarin", "qaya", "haya");
    }

    @Override
    public List<String> yesterdayKeywords() {
        return List.of("qayna", "jainapunchau", "qaynunchay");
    }
}

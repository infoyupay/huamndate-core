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
/**
 * Built-in language support implementations for HumanDate-Core.
 * <p>
 * This package contains ready-to-use {@link com.infoyupay.humandate.core.LanguageSupport}
 * providers that supply localized keywords and unit identifiers for interpreting
 * human-friendly date expressions.
 * </p>
 *
 * <h2>Purpose</h2>
 * <p>
 * HumanDate-Core focuses on ergonomic and fast numeric input.
 * Language support in this package enables additional convenience syntax,
 * such as localized shortcuts for:
 * </p>
 * <ul>
 *   <li>"today" &mdash; e.g. {@code hoy}, {@code now}, {@code kunan}</li>
 *   <li>relative dates &mdash; e.g. {@code +2d}, {@code -1m}, {@code +3p}</li>
 *   <li>yesterday or tomorrow, when defined by the language</li>
 * </ul>
 *
 * <h3>Included languages</h3>
 * <ul>
 *   <li>{@link com.infoyupay.humandate.core.lang.LanguageSupportES} &mdash; Spanish</li>
 *   <li>{@link com.infoyupay.humandate.core.lang.LanguageSupportEN} &mdash; English</li>
 *   <li>{@link com.infoyupay.humandate.core.lang.LanguageSupportQUE} &mdash; Quechua</li>
 * </ul>
 *
 * <p>
 * Additional languages may be contributed by providing your own
 * {@link com.infoyupay.humandate.core.LanguageSupport} implementation
 * and registering it through {@code HumanDateParser#addLanguageSupport()}.
 * </p>
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
package com.infoyupay.humandate.core.lang;
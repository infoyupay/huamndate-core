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
 * Core components of the HumanDate project.<br>
 * <br>
 * This package provides the fundamental building blocks for parsing and
 * formatting human-friendly date expressions. It includes:
 * <ul>
 *   <li>{@code HumanDateParser}: transforms short forms, relative offsets, and
 *       language-specific keywords into {@code LocalDate}</li>
 *   <li>{@code HumanDateFormatter}: produces localized and natural output for
 *       user-visible dates</li>
 *   <li>{@code LanguageSupport}: pluggable interface to provide idioms,
 *       keywords, and rules for each supported language</li>
 *   <li>{@code Languages}: factory for getting built-in language providers
 *       without exposing implementation classes</li>
 * </ul>
 * <br>
 * Design characteristics:
 * <ul>
 *   <li>Thread-safe and immutable components for predictable usage</li>
 *   <li>No implicit fallbacks: unrecognized expressions return {@code null}</li>
 *   <li>No I/O or system-wide state modifications</li>
 *   <li>Processing rules validated through full unit test coverage</li>
 * </ul>
 * <br>
 * Typical usage:
 * <ol>
 *   <li>Create a parser via {@code new HumanDateParser()}</li>
 *   <li>Optionally set a language using {@code setLanguage(...)} to enable
 *       localized keyword parsing</li>
 *   <li>Call {@code apply(...)} with a user input string to obtain a
 *       {@code LocalDate} or {@code null}</li>
 *   <li>Use {@code HumanDateFormatter} for user-visible representation</li>
 * </ol>
 * <br>
 * All classes in this package follow the documentation and style rules described
 * in {@code .junie/guidelines.md}. Unit tests must accompany any change.
 *
 * @author David Vidal, Infoyupay
 * @version 1.0
 */
package com.infoyupay.humandate.core;

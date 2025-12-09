/**
 * The HumanDate Core module provides a lightweight and ergonomic API
 * for formatting and parsing {@link java.time.LocalDate} values.<br>
 * <br>
 * This module focuses exclusively on the core logic of converting
 * human-readable date strings into {@link java.time.LocalDate} objects
 * and vice versa, without dependencies on UI frameworks or external libraries.<br>
 * It is designed to be easily adoptable in JVM applications and to serve as a
 * foundation for higher-level integrations such as JavaFX controls or DSL
 * extensions.<br>
 * <br>
 * Design principles:
 * <ul>
 *   <li>Minimalism: small API surface focused only on what matters</li>
 *   <li>Ergonomics: intuitive for humans to read and write dates</li>
 *   <li>Compatibility: requires only Java 17, no extra dependencies</li>
 *   <li>Extensibility: higher-level modules can build upon the core</li>
 * </ul>
 * <br>
 * {@code HumanDateParser} and {@code HumanDateFormatter} do not expose
 * internal implementation classes, ensuring a clean and stable public API.<br>
 * <br>
 * All documentation follows the style and formatting rules described in
 * {@code .junie/guidelines.md}.
 *
 * @author David Vidal, Infoyupay
 */
module com.infoyupay.humandate.core {
    exports com.infoyupay.humandate.core;
}

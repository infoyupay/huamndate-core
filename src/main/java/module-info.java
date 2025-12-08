/**
 * The HumanDate Core module provides a lightweight, ergonomic API
 * for formatting and parsing {@link java.time.LocalDate} values.
 * <p>
 * This module focuses exclusively on the core logic of converting
 * human-readable date strings into {@link java.time.LocalDate} objects
 * and vice versa, without dependencies on UI frameworks or external libraries.
 * It is designed to be easily adoptable in JVM applications,
 * and to serve as a foundation for higher-level integrations such as
 * JavaFX controls or DSL extensions.
 * </p>
 *
 * <h2>Key design principles</h2>
 * <ul>
 *   <li>Minimalism – small API surface focused only on what matters</li>
 *   <li>Ergonomics – intuitive for humans to read and write dates</li>
 *   <li>Compatibility – requires only Java 17, no extra dependencies</li>
 *   <li>Extensibility – higher-level modules can build upon the core</li>
 * </ul>
 *
 * <p>
 * <strong>Note:</strong> This module does not expose
 * internal implementation details, ensuring a clean and stable public API.
 * </p>
 *
 * @author David Vidal, InfoYupay
 */
module humandate.core {
    //exports com.infoyupay.humandate.core;
}

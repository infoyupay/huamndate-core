# HumanDate – Development Guidelines for Junie

This document summarizes all **project‑specific** rules and guidelines that MUST be followed for ongoing development of the **HumanDate** library.

---

## 🧱 Build & Configuration

<ul>
<li>Project uses **Gradle** with **Java 21+**.</li>
<li>All modules must compile without warnings whenever possible.</li>
<li>Always run minimal compile validation after any structural change:
<br>
{@code com.infoyupay.humandate.core.CodeAlive.codeAlive()}
<br>
This ensures the project still builds and loads classes correctly.</li>
</ul>

---

## 🧪 Testing Guidelines

<ul>
<li>Testing framework: **JUnit 6.0.1**</li>
<li>Assertions library: **AssertJ 3.27.6**</li>
<li>All test classes and test methods MUST include **full Javadocs**</li>
<li>Test sources follow the same comment and style rules as production sources</li>
<li>Tests must be fast, hermetic and without I/O side‑effects</li>
<li>Prefer expressive test names: {@code shouldFormatTodayInEnglish()}</li>
<li>After every change, run the full test suite + the minimal CodeAlive check</li>
</ul>

### Running Tests

<ol>
<li>{@code ./gradlew test}</li>
<li>Check coverage and verify results in build reports</li>
</ol>

### Adding New Tests

<ul>
<li>Use the existing sample test as a template</li>
<li>Test all supported languages when changing localizable behavior</li>
<li>Edge cases must be covered (nulls, past/future boundaries, format fallback)</li>
</ul>

---

## 📝 Code Style and Documentation Rules

All comments and documentation MUST respect the following:

<ul>
<li>**Language**: English only</li>
<li>**Javadocs required everywhere**, including tests</li>
<li>Use {@code} instead of &lt;code&gt; </li>
<li>Use &lt;br&gt; for paragraph breaks (avoid blank lines)</li>
<li>Use &lt;ul&gt;/&lt;li&gt; bullet lists, never hyphens</li>
<li>Use &lt;ol&gt;/&lt;li&gt; for numbered steps</li>
<li>No double blank lines</li>
<li>Respect existing package structure and encapsulation</li>
</ul>

---

## 🔄 Development Flow

<ol>
<li>Implement or modify feature</li>
<li>Update or add tests</li>
<li>Ensure Javadocs updated</li>
<li>Run CodeAliveTest minimal validation</li>
<li>Propose commit messages using **Conventional Commits** format, but NEVER directly commit</li>
</ol>

---

## 📌 Additional Notes

<ul>
<li>Factory class {@code Languages} stays in **core** module to avoid exposing internal implementations</li>
<li>Maintain a clean API surface: keep internals package‑private when possible</li>
<li>All formatters must remain **thread‑safe and immutable**</li>
<li>No localization text duplicates: keep strings inside {@code LanguageSupport} implementations only</li>
</ul>


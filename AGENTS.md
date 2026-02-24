# Repository Guidelines

## Project Structure & Module Organization
This is a Maven-based Java 17 project (`pom.xml`) with a Spring Boot + JavaFX desktop app entrypoint.

- `src/main/java/io/github/dkaukov/cw_decoder_proto/`
- `fx/`: JavaFX UI bootstrap, controllers, and model wiring (`FXMain`, `MainController`)
- `tci/`: TCI websocket client/state parsing
- `liquid/`: DSP and native-library integration utilities
- `src/main/resources/`: `application.yaml`, FXML, icons, and native libs
- `src/test/java/...`: JUnit/Spring Boot tests, organized by package (`tci`, `liquid`)

## Build, Test, and Development Commands
- `mvn clean compile`: compile main code and verify dependency setup.
- `mvn test`: run unit/integration tests under Surefire.
- `mvn -Dtest=TCITrxStateTest test`: run a single test class.
- `mvn spring-boot:run`: launch the app via Spring Boot plugin.
- `mvn clean package`: build packaged artifact in `target/`.

Note: `pom.xml` targets Java 17. Running tests on newer JDKs may require extra Mockito agent setup.

## Coding Style & Naming Conventions
- Follow package prefix `io.github.dkaukov.cw_decoder_proto`.
- Use `UpperCamelCase` for classes, `lowerCamelCase` for methods/fields, and `UPPER_SNAKE_CASE` for constants.
- Keep class names feature-oriented (`TCIClient`, `MainController`, `LiquidDSPFFt`).
- Match surrounding formatting style in edited files (do not reformat unrelated code).
- No formatter/linter plugin is currently enforced in Maven; keep changes minimal and readable.

## Testing Guidelines
- Frameworks: JUnit 5 + Spring Boot Test (`@SpringBootTest`).
- Place tests under matching package paths in `src/test/java`.
- Test class naming: `<ClassName>Test`; test methods should describe behavior (`parseIqSampleRate`).
- Cover parser/state transitions and DSP edge cases with deterministic inputs.

## Commit & Pull Request Guidelines
- Current history uses short messages (`wip`, `first commit`), but contributors should use clear imperative summaries.
- Recommended commit format: `module: concise action` (example: `tci: validate malformed dds payload`).
- PRs should include:
- what changed and why
- test evidence (`mvn test` output or targeted test command)
- screenshots/GIFs for UI-visible JavaFX changes
- linked issue/task when applicable

## Configuration & Runtime Notes
- Default TCI endpoint is in `src/main/resources/application.yaml` (`tci.address: ws://localhost:40001`).
- Keep environment-specific overrides out of committed defaults.

# Repository Guidelines

## Project Structure & Module Organization
This repository is a Maven multi-module project with three modules:

- `liquiddsp-java/`: JNA bindings, native loader, FIR/filter APIs, and bundled LiquidDSP binaries (`src/main/resources/native/**`).
- `tci-java/`: TCI websocket client, parser/state models, and IQ payload types.
- `cw-decoder/`: Spring Boot + JavaFX application (UI, TCI client/state, app services).

Code uses package prefix `io.github.dkaukov.cw_decoder_proto`.

Key paths:
- `liquiddsp-java/src/main/java/.../liquid/`
- `tci-java/src/main/java/.../tci/`
- `cw-decoder/src/main/java/.../{fx,services}/`
- `cw-decoder/src/main/resources/` (`application.yaml`, FXML, icons)
- `liquiddsp-java/scripts/` (native binary build scripts)

## Build, Test, and Development Commands
- `mvn clean compile`: compile all modules from repo root.
- `mvn test`: run all tests across both modules.
- `mvn -pl liquiddsp-java test`: run binding/native tests only.
- `mvn -pl tci-java test`: run TCI parser/client tests only.
- `mvn -pl cw-decoder -Dtest=TCITrxStateTest test`: run a specific app test.
- `mvn -pl cw-decoder spring-boot:run`: start desktop app module.
- `liquiddsp-java/scripts/build-all.sh`: rebuild Linux/Windows native binaries into `liquiddsp-java/src/main/resources/native/`.

## Coding Style & Naming Conventions
- Java 17, standard 4-space indentation, UTF-8 source files.
- Class names: `UpperCamelCase`; methods/fields: `lowerCamelCase`; constants: `UPPER_SNAKE_CASE`.
- Keep bindings thin and explicit in `liquiddsp-java`; keep UI/TCI behavior in `cw-decoder`.
- Avoid unrelated formatting changes in touched files.

## Testing Guidelines
- Frameworks: JUnit 5 and Spring Boot Test.
- Place tests in matching module/package paths.
- Naming: `<ClassName>Test`; behavior-driven method names.
- For native code changes, run `mvn -pl liquiddsp-java test` and then full `mvn test`.

## Commit & Pull Request Guidelines
- Use clear imperative messages with module scope, e.g.:
  - `liquiddsp-java: add firpfb wrapper`
  - `cw-decoder: harden TCI parser for empty payload`
- PRs should include:
  - concise summary and rationale
  - test evidence (`mvn test` or targeted command output)
  - screenshots for JavaFX UI changes
  - linked issue/task when applicable

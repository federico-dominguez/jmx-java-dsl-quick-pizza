# jmx-java-dsl-quick-pizza

JMeter Java DSL demo project that shows a minimal Maven/Java setup and a first smoke test hitting the Quick Pizza site home page: `https://quickpizza.grafana.com/`.

## Summary
This repository is a small demo to get started with the JMeter Java DSL. It contains:
- A minimal Maven `pom.xml` (project coordinates: `com.fedd:jmx-java-dsl-quick-pizza`).
- A simple "Hello World" main class (`src/main/java/com/fedd/App.java`).
- A first JUnit 5 test using the JMeter Java DSL that performs a single HTTP request to the Quick Pizza homepage (`src/test/java/com/fedd/AppTest.java`).

## Prerequisites
- Java 11 (JDK 11)
- Apache Maven 3.6+
- Network access to `https://quickpizza.grafana.com/` for the provided demo test
- Recommended: an IDE such as IntelliJ IDEA or VS Code with Java support

## Repository layout
- `pom.xml` — Maven project file with dependencies
- `src/main/java/com/fedd/App.java` — example main class
- `src/test/java/com/fedd/AppTest.java` — example JUnit 5 / JMeter Java DSL smoke test
- `README.md` — this file

## Quick start (PowerShell)
Open a PowerShell prompt in the repository root (for example `c:\Users\Fede\OneDrive\Documents\jmx-java-dsl-quick-pizza`).

Create standard Maven directories (if missing)
```powershell
New-Item -ItemType Directory -Path src\main\java\com\fedd -Force
New-Item -ItemType Directory -Path src\test\java\com\fedd -Force# jmx-java-dsl-quick-pizza
Jmeter Java DSL demo for performance testing on Quick Pizza website.

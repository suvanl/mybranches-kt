# mybranches-kt

`mb` is a Kotlin/Native (`macosArm64`) CLI tool. It lists local git branches matching `$USER/*`, lets the user select
one through an interactive TUI, and runs `git switch`. The TUI is built
with [Mosaic](https://github.com/JakeWharton/mosaic) (Compose for terminal).

## Build and run

```sh
./gradlew macosArm64Binaries          # compile (debug + release)
./gradlew macosArm64Test              # run tests
```

Binary output: `build/bin/macosArm64/debugExecutable/mybranches.kexe`

## Writing unit tests

Test case names typically start with `should`.

Write all tests in BDD style - should contain `// Given` / `// When` / `// Then` comments:

```kotlin
fun shouldReportSuccess() {
    // Given
    givenProgramInState(ProgramState.SOME_STATE)
    
    // When
    val result = underTest.methodUnderTest()
    
    // Then
    result shouldBe "success"
}
```

Private helper functions should be at the bottom of the test class.

Testing technologies:
- kotlin-test: as a test runner
- Kotest: for assertions
- MockK: for mocking

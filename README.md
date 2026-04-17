# mybranches-kt

## Development

### Build and run the project

From the root project directory, build a debug executable for the desired compilation target using the
`linkDebugExecutable<TargetName>` command:

```shell
./gradlew linkDebugExecutableMacosArm64
```

To build both debug and release executables, use the `<targetName>Binaries` lifecycle task:

```shell
./gradlew macosArm64Binaries
```

> [!NOTE]
> Release binaries are highly optimised. As a result, [compilation of release binaries takes an order of magnitude more
> time than debug binaries](https://kotlinlang.org/docs/native-improving-compilation-time.html#don-t-build-unnecessary-release-binaries).

Binary output lives under `build/bin/<targetName>/debugExecutable/` and `releaseExecutable/` when built.

To run the project, execute the `build/bin/<targetName>/debugExecutable/mb.kexe` command for the desired target, e.g.

```shell
build/bin/macosArm64/debugExecutable/mb.kexe
```

### Run the tests

Run `<targetName>Test` to run all tests in the project for the given target, e.g.

```shell
./gradlew macosArm64Test
```

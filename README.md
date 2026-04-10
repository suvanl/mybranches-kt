# mybranches-kt

## Build and run the project

From the root project directory, run the `<targetName>Binaries` build command for the desired compilation target, e.g.

```shell
./gradlew macosArm64Binaries
```

This command creates the `build/bin/targetName>` directory with two directories inside: `debugExecutable` and
`releaseExecutable`. They contain the corresponding binary files.

By default, the name of the binary file is the same as the project directory.

To run the project, execute the `build/bin/<targetName>/debugExecutable/<projectName>.kexe` command for your target,
for example:

```shell
build/bin/macosArm64/debugExecutable/mybranches.kexe
```

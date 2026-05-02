# mybranches-kt

🌳 Interactive local branch explorer with fuzzy search

![demo](demo.gif)

## Installation

> [!NOTE]
> Currently only macOS (Apple Silicon) and Linux (x86_64) are supported.

### Download the latest release

#### macOS
```shell
curl -fsSL https://github.com/suvanl/mybranches-kt/releases/latest/download/mb-macos-arm64.tar.gz | tar xz
sudo mv mb /usr/local/bin/
```

#### Linux
```shell
curl -fsSL https://github.com/suvanl/mybranches-kt/releases/latest/download/mb-linux-x64.tar.gz | tar xz
sudo mv mb /usr/local/bin/
```

### Download a specific release

#### macOS
```shell
curl -fsSL https://github.com/suvanl/mybranches-kt/releases/download/v<VERSION>/mb-macos-arm64.tar.gz | tar xz
sudo mv mb /usr/local/bin/
```

#### Linux
```shell
curl -fsSL https://github.com/suvanl/mybranches-kt/releases/download/v<VERSION>/mb-linux-x64.tar.gz | tar xz
sudo mv mb /usr/local/bin/
```

Alternatively, download the binary from the [Releases](https://github.com/suvanl/mybranches-kt/releases) page.

### Build from source

See the [Development](#development) section.

## Usage

1. Run `mb` within a git repo
2. Select the desired branch (using <kbd>↑</kbd><kbd>↓</kbd> or <kbd>k</kbd><kbd>j</kbd>)
3. Hit <kbd>⏎ Enter</kbd> to switch to it

### Searching

It's possible to fuzzy search the list, by pressing <kbd>/</kbd> and entering your search query. To lock the filter
(e.g. to allow for navigation within the subset with <kbd>k</kbd> and <kbd>j</kbd>), hit <kbd>⏎ Enter</kbd>. 
To exit search, hit <kbd>Esc</kbd>.

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

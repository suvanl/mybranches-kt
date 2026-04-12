## Mission

Create a Kotlin/Native CLI tool that allows the user to view a list of their git branches in the repo they run an the
program from (via an `mb` command).

By default, this assumes that the user's branches will be named in the format:
`<username>/<branchname>`, e.g. `jane.doe/cool-feature/bugfix-abc`. If the username is `jane.doe`, we should be listing
all branches starting with `jane.doe`.

The list of branches should be interactive, allowing the user to cycle through the list and hit Enter to switch to the
selected branch.

### Basic workflow example

1. User runs `mb`
2. Lists 10 branches
3. User uses arrow keys to go through the list to select the nth one in the list
4. User hits Enter on the selected list item, resulting in `git switch <selected-branch-name>` being run.

## MVP notes

As a first pass, this will be an MVP. Consider the following as such:

- Only supports the `macosArm64` target.
- Let the username portion of the branch name pattern be the user's local system username.
    - Allowing the user to provide a custom username and/or branch name pattern is out of scope.
- Support arrow keys for list navigation only.
    - Vim keybindings are out of scope.
- Custom branch naming patterns are not supported - out of scope.
- Eventually, the user can specify a flag which will first delete any local branches gone from remote before listing the
  remaining local branches. This is **out of scope** for MVP.

## Resources

- Kotlin/Native docs:
  /Users/suvan/gh/extern/JetBrains/kotlin-web-site/docs/topics/native
- Mosaic (for TUI): /Users/suvan/gh/extern/JakeWharton/mosaic

## Architecture

- Ensure separation of concerns between UI and non-UI components.
- Ensure testability of components.
- Non-blocking UI
  - Remains responsive and does not hang while operations (e.g. git commands) are running in the background.

## Verification

- Basic workflow example works
    - only lists branches matching the pattern
- If no branches matching pattern, user is informed and program terminates gracefully.
- If git errors out, proxy the full error through to the user and terminate the program.
- Gracefully handles git not being installed.
- The list of branches fits on one screen.
  - Pagination is used if it isn't possible to fit the entire list on one screen. 

## Guidance

- Use the AskUserQuestion tool until you are 95% confident in your approach.

## Mission

Make it so that the "help" UI (within HeaderRow) is not visible by default but can be displayed if requested.

Currently, it is visible by default and is controlled by the `showHelp` param in `HeaderRow` (
com/suvanl/mybranches/ui/HeaderRow.kt:13).

### Basic workflow example

1. User runs `mb`
2. Header row shows `user/*  (ctrl+h for help)`
3. User presses `ctrl+h`
4. Help is displayed in the `HeaderRow`
5. Pressing `ctrl+h` again toggles it back to hidden

### Verification

- Manual smoke test of basic workflow example works.
- Update `BranchListScreenTest#shouldDisplayBranchList` to ensure help is displayed if `showHelp` is true.
- Add an integration test for this in `AppTest`.
    - Including the toggle-ability of it (e.g. can repeatedly be shown/hidden when in the `AppState.Ready` state)

### Questions

- Is hooking into the `showHelp` param a good idea or would architectural changes be required to implement this in a
  better way?
- Is `ctrl+h` a safe/valid/understandable key combo for this?
    - **Answered**: `?` is more standard for TUI tools - let's use that instead
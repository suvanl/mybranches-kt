package com.suvanl.mybranches.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.suvanl.mybranches.git.GitClient
import com.suvanl.mybranches.git.GitError
import kotlinx.coroutines.awaitCancellation

@Composable
fun App(
    gitClient: GitClient,
    branchNamePattern: String,
    onExit: (AppState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf<AppState>(AppState.Loading) }
    val terminalRows = LocalTerminalState.current.size.rows
    val pageSize = (terminalRows - 2).coerceAtLeast(1)

    LaunchedEffect(Unit) {
        state = try {
            val branches = gitClient.listBranches(branchNamePattern)
            if (branches.isEmpty()) {
                AppState.Empty
            } else {
                val currentIndex = branches.indexOfFirst { it.isCurrent }.takeIf { it >= 0 } ?: 0
                AppState.Ready(branches, selectedItemIndex = currentIndex, pageStartIndex = 0)
            }
        } catch (e: GitError) {
            AppState.Failed(e.message ?: "unknown git error")
        } catch (e: IllegalStateException) {
            AppState.Failed(e.message ?: "error")
        }
    }

    val switchingTarget = (state as? AppState.Switching)?.target
    if (switchingTarget != null) {
        LaunchedEffect(switchingTarget) {
            state = try {
                gitClient.switchBranch(switchingTarget)
                AppState.Switched(switchingTarget)
            } catch (e: GitError) {
                AppState.Failed(e.message ?: "unknown git error")
            }
        }
    }

    SideEffect {
        if (state.isTerminalState()) {
            onExit(state)
        }
    }

    Column(
        modifier = modifier
            .onKeyEvent { event ->
                val ready = state as? AppState.Ready ?: return@onKeyEvent false
                when (event) {
                    KeyEvent("ArrowUp") -> {
                        state = ready.moveUp()
                        true
                    }

                    KeyEvent("ArrowDown") -> {
                        state = ready.moveDown(pageSize)
                        true
                    }

                    KeyEvent("Enter") -> {
                        state = AppState.Switching(ready.branches[ready.selectedItemIndex].name)
                        true
                    }

                    KeyEvent("q"), KeyEvent("Escape") -> {
                        state = AppState.Cancelled
                        true
                    }

                    else -> false
                }
            },
    ) {
        when (val s = state) {
            AppState.Loading -> Text("Loading...")

            AppState.Empty -> Text("No branches matching '$branchNamePattern'")

            is AppState.Ready -> {
                HeaderRow(
                    branchPattern = branchNamePattern,
                    help = "↑↓ navigate | enter select | q quit",
                    showHelp = true,
                )
                BranchList(
                    branches = s.branches,
                    selected = s.selectedItemIndex,
                    pageStart = visiblePageStart(s.selectedItemIndex, s.pageStartIndex, pageSize),
                    pageSize = pageSize,
                )
            }

            is AppState.Switching -> {
                // FIXME: if `git switch` happens quick, this flashes for a very brief moment.
                //  Maybe only show if it takes longer than n milliseconds
                Text("Switching to ${s.target}...")
            }

            is AppState.Switched -> Text("Switched to branch '${s.target}'")

            is AppState.Failed, AppState.Cancelled -> {}
        }
    }

    if (!state.isTerminalState()) {
        LaunchedEffect(Unit) { awaitCancellation() }
    }
}

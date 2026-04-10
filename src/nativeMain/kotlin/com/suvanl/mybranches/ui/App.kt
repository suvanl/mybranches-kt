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
    username: String,
    onExit: (AppState) -> Unit,
) {
    var state by remember { mutableStateOf<AppState>(AppState.Loading) }
    val terminalRows = LocalTerminalState.current.size.rows
    val pageSize = (terminalRows - 2).coerceAtLeast(1)

    LaunchedEffect(Unit) {
        state = try {
            val branches = gitClient.listBranches(username)
            if (branches.isEmpty()) {
                AppState.Empty
            } else {
                val currentIdx = branches.indexOfFirst { it.current }.takeIf { it >= 0 } ?: 0
                AppState.Ready(branches, selected = currentIdx, pageStart = 0)
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

    val isTerminal = state is AppState.Switched ||
        state is AppState.Failed ||
        state is AppState.Cancelled ||
        state is AppState.Empty

    SideEffect {
        if (isTerminal) onExit(state)
    }

    val keyModifier = if (state is AppState.Ready) {
        Modifier.onKeyEvent { event ->
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
                    state = AppState.Switching(ready.branches[ready.selected].name)
                    true
                }

                KeyEvent("q"), KeyEvent("Escape") -> {
                    state = AppState.Cancelled
                    true
                }

                else -> false
            }
        }
    } else {
        Modifier
    }

    Column(modifier = keyModifier) {
        when (val s = state) {
            AppState.Loading -> Text("Loading...")

            AppState.Empty -> Text("No branches matching '$username/*'")

            is AppState.Ready -> {
                Text("mybranches — ↑↓ navigate  enter select  q quit")
                val visiblePageStart = visiblePageStart(s.selected, s.pageStart, pageSize)
                BranchList(s.branches, s.selected, visiblePageStart, pageSize)
            }

            is AppState.Switching -> Text("Switching to ${s.target}...")

            is AppState.Switched, is AppState.Failed, AppState.Cancelled -> {}
        }
    }

    if (!isTerminal) {
        LaunchedEffect(Unit) { awaitCancellation() }
    }
}

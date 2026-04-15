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
    var showHelp by remember { mutableStateOf(false) }

    val terminalRows = LocalTerminalState.current.size.rows
    // Subtract 3 to account for:
    //  - 1 row for the header
    //  - 1 for the pagination counter
    //  - 1 for the terminal cursor row (Mosaic behaviour)
    val pageSize = (terminalRows - 3).coerceAtLeast(1)

    LaunchedEffect(Unit) {
        state = try {
            val branches = gitClient.listBranches(branchNamePattern)
            if (branches.isEmpty()) {
                AppState.Empty
            } else {
                val currentIndex = branches.indexOfFirst { it.isCurrent }.coerceAtLeast(0)
                val prefix = branchNamePattern.substringBefore("*")
                AppState.Ready(
                    branches = branches,
                    selectedItemIndex = currentIndex,
                    pageStartIndex = 0,
                    branchPrefix = prefix,
                )
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
                val readyState = state as? AppState.Ready ?: return@onKeyEvent false

                /*
                If searchState is Active, we want '?' to register as part of the search query, in contrast to the
                standard "toggle help" behaviour
                 */
                if (readyState.searchState !is SearchState.Active && event == KeyEvent("?")) {
                    showHelp = !showHelp
                    return@onKeyEvent true
                }

                return@onKeyEvent readyState.handleKeyEvent(event, pageSize)?.let { newState ->
                    state = newState
                    true
                } ?: false
            },
    ) {
        when (val appState = state) {
            AppState.Loading -> Text("Loading...")

            AppState.Empty -> Text("No branches matching '$branchNamePattern'")

            is AppState.Ready -> {
                BranchListScreen(
                    state = appState,
                    pattern = branchNamePattern,
                    pageSize = pageSize,
                    showHelp = showHelp,
                )
            }

            is AppState.Switching -> {
                // FIXME: if `git switch` happens quick, this flashes for a very brief moment.
                //  Maybe only show if it takes longer than n milliseconds
                Text("Switching to ${appState.target}...")
            }

            is AppState.Switched -> Text("Switched to branch '${appState.target}'")

            is AppState.Failed, AppState.Cancelled -> {}
        }
    }

    if (!state.isTerminalState()) {
        LaunchedEffect(Unit) { awaitCancellation() }
    }
}

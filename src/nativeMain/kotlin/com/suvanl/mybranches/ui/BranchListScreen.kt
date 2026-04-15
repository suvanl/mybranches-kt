package com.suvanl.mybranches.ui

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.ColumnScope

@Suppress("UnusedReceiverParameter")
@Composable
fun ColumnScope.BranchListScreen(
    state: AppState.Ready,
    pattern: String,
    pageSize: Int,
    showHelp: Boolean,
    modifier: Modifier = Modifier,
) {
    HeaderRow(
        branchPattern = pattern,
        help = "↑/k ↓/j navigate | enter select | / search | q quit",
        showHelp = showHelp,
        searchState = state.searchState,
    )
    BranchList(
        branches = state.displayedBranches,
        selected = state.selectedItemIndex,
        pageStart = visiblePageStart(state.selectedItemIndex, state.pageStartIndex, pageSize),
        pageSize = pageSize,
        modifier = modifier,
    )
}

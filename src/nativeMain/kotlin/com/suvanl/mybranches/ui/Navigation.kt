package com.suvanl.mybranches.ui

/**
 * Ensures the selected item is always within the visible page.
 */
internal fun visiblePageStart(
    selectedIndex: Int,
    pageStartIndex: Int,
    pageSize: Int,
): Int = when {
    selectedIndex < pageStartIndex -> selectedIndex
    selectedIndex >= pageStartIndex + pageSize -> selectedIndex - pageSize + 1
    else -> pageStartIndex
}.coerceAtLeast(0)

internal fun AppState.Ready.moveUp(): AppState.Ready {
    val newSelected = (selectedItemIndex - 1).coerceAtLeast(0)
    val newPageStart = if (newSelected < pageStartIndex) {
        newSelected
    } else {
        pageStartIndex
    }
    return copy(selectedItemIndex = newSelected, pageStartIndex = newPageStart)
}

/**
 * @param currentPageSize Number of currently visible rows in the terminal window
 */
internal fun AppState.Ready.moveDown(currentPageSize: Int): AppState.Ready {
    val newSelected = (selectedItemIndex + 1).coerceAtMost(branches.size - 1)
    val pageEnd = pageStartIndex + currentPageSize
    val newPageStart = if (newSelected >= pageEnd) {
        pageStartIndex + 1
    } else {
        pageStartIndex
    }
    return copy(selectedItemIndex = newSelected, pageStartIndex = newPageStart)
}

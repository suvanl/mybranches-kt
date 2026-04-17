package com.suvanl.mybranches.ui

import com.jakewharton.mosaic.layout.KeyEvent

internal fun AppState.Ready.handleKeyEvent(
    event: KeyEvent,
    pageSize: Int,
): AppState? = when (searchState) {
    is SearchState.Active -> handleActiveSearchKey(event, pageSize)
    is SearchState.Filtered, SearchState.Inactive -> handleNormalKey(event, pageSize)
}

private fun AppState.Ready.handleActiveSearchKey(
    event: KeyEvent,
    pageSize: Int,
): AppState? {
    val activeSearch = searchState as SearchState.Active
    return when (event) {
        KeyEvent(key = "Escape"), KeyEvent(key = "Enter") -> {
            if (activeSearch.query.isEmpty()) {
                copy(searchState = SearchState.Inactive)
            } else {
                copy(searchState = SearchState.Filtered(activeSearch.query))
            }
        }

        KeyEvent(key = "Backspace") -> {
            val newQuery = activeSearch.query.dropLast(1)
            copy(searchState = SearchState.Active(newQuery), selectedItemIndex = 0, pageStartIndex = 0)
        }

        KeyEvent(key = "ArrowUp") -> moveUp()

        KeyEvent(key = "ArrowDown") -> moveDown(pageSize)

        KeyEvent(ctrl = true, key = "c") -> AppState.Cancelled

        else -> {
            val isPlainCharacter = event.key.length == 1 && !event.ctrl && !event.alt
            if (!isPlainCharacter) {
                return null
            }
            val newQuery = activeSearch.query + event.key
            copy(searchState = SearchState.Active(newQuery), selectedItemIndex = 0, pageStartIndex = 0)
        }
    }
}

private fun AppState.Ready.handleNormalKey(
    event: KeyEvent,
    pageSize: Int,
): AppState? = when (event) {
    KeyEvent("/") -> {
        val resumeQuery = searchState.query
        copy(searchState = SearchState.Active(resumeQuery))
    }

    KeyEvent("ArrowUp"), KeyEvent("k") -> moveUp()

    KeyEvent("ArrowDown"), KeyEvent("j") -> moveDown(pageSize)

    KeyEvent("Enter") -> {
        val displayed = displayedBranches
        if (displayed.isNotEmpty()) {
            AppState.Switching(displayed[selectedItemIndex].name)
        } else {
            null
        }
    }

    KeyEvent("Escape") -> {
        if (searchState.query.isNotEmpty()) {
            copy(searchState = SearchState.Inactive, selectedItemIndex = 0, pageStartIndex = 0)
        } else {
            AppState.Cancelled
        }
    }

    KeyEvent("q"), KeyEvent("c", ctrl = true) -> AppState.Cancelled

    else -> null
}

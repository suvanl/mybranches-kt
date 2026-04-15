package com.suvanl.mybranches.ui

sealed interface SearchState {
    data object Inactive : SearchState
    data class Active(val query: String) : SearchState
    data class Filtered(val query: String) : SearchState
}

internal val SearchState.query: String
    get() = when (this) {
        SearchState.Inactive -> ""
        is SearchState.Active -> query
        is SearchState.Filtered -> query
    }

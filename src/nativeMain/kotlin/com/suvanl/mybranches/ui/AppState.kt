package com.suvanl.mybranches.ui

import com.suvanl.mybranches.git.Branch

sealed interface AppState {
    /**
     * Initial state
     */
    data object Loading : AppState

    /**
     * Fetch succeeded but no matching branches to be displayed
     */
    data object Empty : AppState

    /**
     * List of branches loaded and navigable
     */
    data class Ready(
        val branches: List<Branch>,
        val selectedItemIndex: Int,
        val pageStartIndex: Int,
    ) : AppState

    /**
     * User requested branch switch, `git switch` is running
     */
    data class Switching(val target: String) : AppState

    /**
     * `git switch` succeeded
     */
    data class Switched(val target: String) : AppState

    /**
     * Error state
     */
    data class Failed(val message: String) : AppState

    /**
     * Graceful quit/exit state
     */
    data object Cancelled : AppState

    fun isTerminalState(): Boolean = this is Switched ||
        this is Failed ||
        this is Cancelled ||
        this is Empty
}

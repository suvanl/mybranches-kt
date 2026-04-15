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
        val branchPrefix: String = "",
        val searchState: SearchState = SearchState.Inactive,
    ) : AppState {
        val displayedBranches: List<Branch>
            get() {
                val searchQuery = searchState.query
                return if (searchQuery.isBlank()) {
                    branches
                } else {
                    branches.filter { branch ->
                        /*
                        Since all branches in the list will start with the same pattern (username/ *), it may make
                        searching slightly easier if we ignore this prefix. However, if we ever support custom branch
                        name patterns to be supplied, we'll need to review this as it's likely there'll be no
                        guarantees that the pattern will be some kind of prefix.
                         */
                        branch.name.removePrefix(branchPrefix).fuzzyContains(searchQuery)
                    }
                }
            }
    }

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

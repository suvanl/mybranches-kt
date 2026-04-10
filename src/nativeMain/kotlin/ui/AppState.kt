package ui

import git.Branch

sealed interface AppState {
    data object Loading : AppState
    data object Empty : AppState
    data class Ready(
        val branches: List<Branch>,
        val selected: Int,
        val pageStart: Int,
    ) : AppState
    data class Switching(val target: String) : AppState
    data class Switched(val target: String) : AppState
    data class Failed(val message: String) : AppState
    data object Cancelled : AppState
}

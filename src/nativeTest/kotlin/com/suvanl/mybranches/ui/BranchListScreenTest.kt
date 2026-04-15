package com.suvanl.mybranches.ui

import com.jakewharton.mosaic.testing.runMosaicTest
import com.jakewharton.mosaic.ui.Column
import com.suvanl.mybranches.git.Branch
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class BranchListScreenTest {

    @Test
    fun shouldDisplayBranchList() = runTest {
        runMosaicTest {
            // Given
            setContent {
                Column {
                    BranchListScreen(
                        state = AppState.Ready(
                            branches = listOf(
                                Branch(
                                    name = "user/branch1",
                                    isCurrent = true,
                                ),
                                Branch(
                                    name = "user/branch2",
                                    isCurrent = false,
                                ),
                            ),
                            selectedItemIndex = 0,
                            pageStartIndex = 0,
                        ),
                        pattern = "user/*",
                        pageSize = 4,
                        showHelp = false,
                    )
                }
            }

            // When
            val snapshot = awaitSnapshot()

            // Then
            // ... displays the branch name pattern in use
            snapshot shouldContain "user/*"

            // ... displays the help hint
            snapshot shouldContain "(? for help)"

            // ... displays the list of branches
            snapshot shouldContain """
                > * user/branch1
                    user/branch2
            """.trimIndent()
        }
    }

    @Test
    fun shouldShowSearchQueryInHeaderWhenSearching() = runTest {
        runMosaicTest {
            // Given
            setContent {
                Column {
                    BranchListScreen(
                        state = givenReadyState(searchState = SearchState.Active("feat")),
                        pattern = "user/*",
                        pageSize = 4,
                        showHelp = false,
                    )
                }
            }

            // When
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "/ feat█"
        }
    }

    @Test
    fun shouldShowFilteredResultsOnly() = runTest {
        runMosaicTest {
            // Given
            setContent {
                Column {
                    BranchListScreen(
                        state = givenReadyState(searchState = SearchState.Filtered("branch1")),
                        pattern = "user/*",
                        pageSize = 4,
                        showHelp = false,
                    )
                }
            }

            // When
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "user/branch1"
            snapshot shouldNotContain "user/branch2"
        }
    }

    @Test
    fun shouldShowNoMatchingBranchesWhenFilterMatchesNothing() = runTest {
        runMosaicTest {
            // Given
            setContent {
                Column {
                    BranchListScreen(
                        state = givenReadyState(searchState = SearchState.Active("zzz")),
                        pattern = "user/*",
                        pageSize = 4,
                        showHelp = false,
                    )
                }
            }

            // When
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "No matching branches"
        }
    }

    @Test
    fun shouldShowHelpWhenRequested() = runTest {
        runMosaicTest {
            // Given
            setContent {
                Column {
                    BranchListScreen(
                        state = AppState.Ready(
                            branches = listOf(
                                Branch(
                                    name = "user/branch1",
                                    isCurrent = true,
                                ),
                                Branch(
                                    name = "user/branch2",
                                    isCurrent = false,
                                ),
                            ),
                            selectedItemIndex = 0,
                            pageStartIndex = 0,
                        ),
                        pattern = "user/*",
                        pageSize = 4,
                        // ... help is visible
                        showHelp = true,
                    )
                }
            }

            // When
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "↑/k ↓/j navigate | enter select | / search | q quit"
        }
    }

    private fun givenReadyState(
        searchState: SearchState = SearchState.Inactive,
    ) = AppState.Ready(
        branches = listOf(
            Branch(name = "user/branch1", isCurrent = true),
            Branch(name = "user/branch2", isCurrent = false),
        ),
        selectedItemIndex = 0,
        pageStartIndex = 0,
        branchPrefix = "user/",
        searchState = searchState,
    )
}

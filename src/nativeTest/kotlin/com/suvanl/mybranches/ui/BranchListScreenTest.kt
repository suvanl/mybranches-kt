package com.suvanl.mybranches.ui

import com.jakewharton.mosaic.testing.runMosaicTest
import com.jakewharton.mosaic.ui.Column
import com.suvanl.mybranches.git.Branch
import io.kotest.matchers.string.shouldContain
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

            // ... displays the list of branches
            snapshot shouldContain """
                > * user/branch1
                    user/branch2
            """.trimIndent()
        }
    }
}

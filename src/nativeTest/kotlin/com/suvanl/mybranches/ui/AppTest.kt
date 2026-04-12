package com.suvanl.mybranches.ui

import com.jakewharton.mosaic.terminal.KeyboardEvent
import com.jakewharton.mosaic.testing.runMosaicTest
import com.suvanl.mybranches.git.GitClient
import com.suvanl.mybranches.system.CommandRunResult
import com.suvanl.mybranches.system.CommandRunner
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AppTest {

    @Test
    fun shouldShowBranchListAfterLoading() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n user/bugfix\n", success = true),
            )

            // When
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshot() // ... skip loading state
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "user/feature"
            snapshot shouldContain "user/bugfix"

            // TODO: test entire rendered output once stable?
        }
    }

    @Test
    fun shouldShowEmptyMessageWhenNoBranches() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = "", success = true),
            )

            // When
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshot() // ... skip loading state
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "No branches matching 'user/*'"
            exitState shouldBe AppState.Empty
        }
    }

    @Test
    fun shouldMoveCursorOnArrowDown() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/first\n user/second\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshot() // ... skip Loading state
            awaitSnapshot() // ... in Ready state

            // When
            sendKeyEvent(arrowDown)
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain ">   user/second"
        }
    }

    @Test
    fun shouldSwitchBranchOnEnter() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
                switchBranchResult = CommandRunResult(output = "", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshot() // ... skip Loading state
            awaitSnapshot() // ... in Ready state

            // When
            sendKeyEvent(enter)
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "Switching to user/feature"
        }
    }

    @Test
    fun shouldExitOnQ() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshot() // ... skip Loading state
            awaitSnapshot() // ... in Ready state

            // When
            sendKeyEvent(qKey)
            awaitSnapshot()

            // Then
            exitState shouldBe AppState.Cancelled
        }
    }

    @Test
    fun shouldPreSelectCurrentBranch() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(
                    output = " user/other\n*user/current\n user/another\n",
                    success = true,
                ),
            )

            // When
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshot() // ... skip loading state
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "> * user/current"
        }
    }

    private val arrowDown = KeyboardEvent(codepoint = KeyboardEvent.Down)
    private val enter = KeyboardEvent(codepoint = 13)
    private val qKey = KeyboardEvent(codepoint = 'q'.code)

    private class FakeCommandRunner(
        private val listBranchesResult: CommandRunResult,
        private val switchBranchResult: CommandRunResult = CommandRunResult(output = "", success = true),
    ) : CommandRunner {
        override fun run(vararg args: String): CommandRunResult = when (args[1]) {
            "branch" -> listBranchesResult
            "switch" -> switchBranchResult
            else -> error("unexpected command: ${args.toList()}")
        }
    }
}

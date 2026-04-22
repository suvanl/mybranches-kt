package com.suvanl.mybranches.ui

import com.jakewharton.mosaic.terminal.KeyboardEvent
import com.jakewharton.mosaic.testing.TestMosaic
import com.jakewharton.mosaic.testing.runMosaicTest
import com.suvanl.mybranches.git.GitClient
import com.suvanl.mybranches.system.CommandRunResult
import com.suvanl.mybranches.system.CommandRunner
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AppTest {

    private val arrowDown = KeyboardEvent(codepoint = KeyboardEvent.Down)
    private val enter = KeyboardEvent(codepoint = '\r'.code)
    private val escape = KeyboardEvent(codepoint = 0x1B)
    private val qKey = KeyboardEvent(codepoint = 'q'.code)
    private val questionMark = KeyboardEvent(codepoint = '?'.code)
    private val slash = KeyboardEvent(codepoint = '/'.code)

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
            val snapshot = awaitSnapshotWhere { "user/feature" in it }

            // Then
            snapshot shouldContain "user/bugfix"
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
            val snapshot = awaitSnapshotWhere { "No branches matching" in it }

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
            awaitSnapshotWhere { ">   user/first" in it }

            // When
            sendKeyEvent(arrowDown)

            // Then
            shouldNotThrow<IllegalStateException> {
                awaitSnapshotWhere { ">   user/second" in it }
            }
        }
    }

    @Test
    fun shouldSwitchBranchOnEnter() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
                switchBranchResult = CommandRunResult(output = "", success = true),
            )
            setContent {
                App(
                    gitClient = GitClient(runner),
                    branchNamePattern = "user/*",
                    onExit = { /* do nothing */ },
                )
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            sendKeyEvent(enter)
            val snapshot = awaitSnapshotWhere { "Switching to" in it }

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
            awaitSnapshotWhere { "user/feature" in it }

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
            val snapshot = awaitSnapshotWhere { "user/current" in it }

            // Then
            snapshot shouldContain "> * user/current"
        }
    }

    @Test
    fun shouldToggleHelpOnQuestionMark() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            val readySnapshot = awaitSnapshotWhere { "(? for help)" in it }
            readySnapshot shouldNotContain "↑/k ↓/j navigate"

            // When toggle help on
            sendKeyEvent(questionMark)
            val helpShown = awaitSnapshotWhere { "↑/k ↓/j navigate" in it }

            // Then
            helpShown shouldNotContain "(? for help)"

            // When toggle help off
            sendKeyEvent(questionMark)
            val helpHidden = awaitSnapshotWhere { "(? for help)" in it }

            // Then
            helpHidden shouldNotContain "↑/k ↓/j navigate"
        }
    }

    @Test
    fun shouldFilterBranchesWhenSearching() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n user/bugfix\n user/chore\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            sendKeyEvent(slash)
            typeSearchQuery("f")
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "user/feature"
            snapshot shouldContain "user/bugfix"
            snapshot shouldNotContain "user/chore"
        }
    }

    @Test
    fun shouldKeepFilterOnEscapeFromSearchMode() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n user/bugfix\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            // ... search for "feat"
            sendKeyEvent(slash)
            typeSearchQuery("feat")
            // ... hit Esc
            sendKeyEvent(escape)
            val snapshot = awaitSnapshot()

            // Then filter persists
            snapshot shouldContain "user/feature"
            snapshot shouldNotContain "user/bugfix"
        }
    }

    @Test
    fun shouldClearFilterOnEscapeFromNormalMode() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n user/bugfix\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            // ... search
            sendKeyEvent(slash)
            typeSearchQuery("feat")
            // ... lock filter with Escape
            sendKeyEvent(escape)
            awaitSnapshot()
            // ... cancel search filter
            sendKeyEvent(escape)
            val snapshot = awaitSnapshot()

            // Then full list restored
            snapshot shouldContain "user/feature"
            snapshot shouldContain "user/bugfix"
        }
    }

    @Test
    fun shouldQuitOnEscapeWithNoFilter() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            sendKeyEvent(escape)
            awaitSnapshot()

            // Then
            exitState shouldBe AppState.Cancelled
        }
    }

    @Test
    fun shouldKeepFilterOnEnterAndExitSearchMode() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n user/bugfix\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            // ... enter search mode
            sendKeyEvent(slash)
            // ... type query
            typeSearchQuery("bf")
            // ... exit search mode using Enter key (lock filter)
            sendKeyEvent(enter)
            val snapshot = awaitSnapshot()

            // Then filter persists
            snapshot shouldContain "user/bugfix"
            snapshot shouldNotContain "user/feature"

            // When `q` pressed
            sendKeyEvent(qKey)
            awaitSnapshot()
            // Then program quits gracefully
            exitState shouldBe AppState.Cancelled
        }
    }

    @Test
    fun shouldNotExitOnQWhileSearching() = runTest {
        runMosaicTest {
            // Given
            var exitState: AppState? = null
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = { exitState = it })
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            sendKeyEvent(slash)
            sendKeyEvent(qKey)
            awaitSnapshot()

            // Then
            exitState shouldBe null
        }
    }

    @Test
    fun shouldShowNoMatchingBranchesWhenSearchMatchesNothing() = runTest {
        runMosaicTest {
            // Given
            val runner = FakeCommandRunner(
                listBranchesResult = CommandRunResult(output = " user/feature\n", success = true),
            )
            setContent {
                App(gitClient = GitClient(runner), branchNamePattern = "user/*", onExit = {})
            }
            awaitSnapshotWhere { "user/feature" in it }

            // When
            sendKeyEvent(slash)
            typeSearchQuery("zzz")
            val snapshot = awaitSnapshot()

            // Then
            snapshot shouldContain "No matching branches"
        }
    }

    private suspend fun <T : CharSequence> TestMosaic<T>.awaitSnapshotWhere(
        timeout: Duration = 10.seconds,
        predicate: (T) -> Boolean,
    ): T {
        var result: T? = null
        withTimeout(timeout) {
            while (true) {
                val snapshot = awaitSnapshot()
                if (predicate(snapshot)) {
                    result = snapshot
                    break
                }
            }
        }
        return result ?: error("no result")
    }

    private fun <T> TestMosaic<T>.typeSearchQuery(query: String) {
        query.forEach { char ->
            sendKeyEvent(KeyboardEvent(codepoint = char.code))
        }
    }

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

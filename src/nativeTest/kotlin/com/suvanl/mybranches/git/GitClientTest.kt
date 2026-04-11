package com.suvanl.mybranches.git

import com.suvanl.mybranches.system.CommandResult
import com.suvanl.mybranches.system.CommandRunner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GitClientTest {

    @Test
    fun shouldReturnEmptyListWhenGitOutputIsEmpty() = runTest {
        // Given
        val client = givenGitClientReturning(output = "", success = true)

        // When
        val branches = client.listBranches("user/*")

        // Then
        branches should beEmpty()
    }

    @Test
    fun shouldParseBranchNamesFromGitOutput() = runTest {
        // Given
        val client = givenGitClientReturning(output = " user/feature\n user/bugfix/2\n user/won'tfix\n", success = true)

        // When
        val branches = client.listBranches("user/*")

        // Then
        val branchNames = branches.map { it.name }
        branchNames should containExactly(
            "user/feature",
            "user/bugfix/2",
            "user/won'tfix",
        )
    }

    @Test
    fun shouldMarkCurrentBranchWhenLineStartsWithAsterisk() = runTest {
        // Given
        val client = givenGitClientReturning(output = "* user/current\n user/other\n", success = true)

        // When
        val branches = client.listBranches("user/*")

        // Then
        branches should containExactly(
            Branch(name = "user/current", isCurrent = true),
            Branch(name = "user/other", isCurrent = false),
        )
    }

    @Test
    fun shouldSkipBlankLinesInGitOutput() = runTest {
        // Given
        val client = givenGitClientReturning(output = " user/feature\n\n user/bugfix\n", success = true)

        // When
        val branches = client.listBranches("user/*")

        // Then
        branches shouldHaveSize 2
    }

    @Test
    fun shouldTrimWhitespaceFromBranchNames() = runTest {
        // Given
        val client = givenGitClientReturning(output = "  user/feature  \n", success = true)

        // When
        val branches = client.listBranches("user/*")

        // Then
        branches shouldHaveSize 1
        branches.first() shouldBe Branch(name = "user/feature", isCurrent = false)
    }

    @Test
    fun shouldThrowWhenNotInAGitRepository() = runTest {
        // Given
        val client = givenGitClientReturning(output = "fatal: not a git repository", success = false)

        // When / Then
        val exception = shouldThrowExactly<GitError.NotARepository> {
            client.listBranches("user/*")
        }
        exception.message shouldBe "not a git repository: ."
    }

    @Test
    fun shouldThrowIfGitExitsWithNonZeroExitCode() = runTest {
        // Given
        val client = givenGitClientReturning(output = "some other error", success = false)

        // When
        val error = shouldThrow<GitError.CommandFailed> {
            client.listBranches("user/*")
        }

        // Then
        error.message shouldBe "some other error"
    }

    @Test
    fun shouldUseFallbackMessageWhenCommandFailedOutputIsBlank() = runTest {
        // Given
        val client = givenGitClientReturning(output = "", success = false)

        // When
        val error = shouldThrow<GitError.CommandFailed> {
            client.listBranches("user/*")
        }

        // Then
        error.message shouldBe "git branch failed"
    }

    @Test
    fun shouldInvokeGitBranchWithExpectedArgs() = runTest {
        // Given
        val runner = CapturingRunner(result = CommandResult(output = "", success = true))
        val client = GitClient(runner)

        // When
        client.listBranches("user/*")

        // Then
        runner.capturedArgs should containExactly(
            "git",
            "branch",
            "--list",
            "user/*",
            "--sort=-committerdate",
            "--format=%(HEAD)%(refname:short)",
        )
    }

    @Test
    fun shouldInvokeGitSwitchWithTargetBranch() = runTest {
        // Given
        val runner = CapturingRunner(result = CommandResult(output = "", success = true))
        val client = GitClient(runner)

        // When
        client.switchBranch("user/feature")

        // Then
        runner.capturedArgs shouldBe listOf("git", "switch", "user/feature")
    }

    @Test
    fun shouldThrowIfSwitchFails() = runTest {
        // Given
        val client = givenGitClientReturning(output = "error: pathspec did not match", success = false)

        // When
        val error = shouldThrow<GitError.CommandFailed> {
            client.switchBranch("user/feature")
        }

        // Then
        error.message shouldBe "error: pathspec did not match"
    }

    private fun givenGitClientReturning(output: String, success: Boolean) = GitClient { _ ->
        CommandResult(output, success)
    }

    private class CapturingRunner(private val result: CommandResult) : CommandRunner {
        var capturedArgs: List<String> = emptyList()
            private set

        override fun run(vararg args: String): CommandResult {
            capturedArgs = args.toList()
            return result
        }
    }
}

package com.suvanl.mybranches.git

import com.suvanl.mybranches.system.CommandRunner
import com.suvanl.mybranches.system.runCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitClient(
    private val runner: CommandRunner = { args -> runCommand(*args) },
) {
    suspend fun listBranches(pattern: String): List<Branch> = withContext(Dispatchers.Default) {
        val result = runner.run(
            "git",
            "branch",
            "--list",
            pattern,
            "--sort=-committerdate",
            "--format=%(HEAD)%(refname:short)",
        )

        if (!result.success) {
            if (result.output.contains("not a git repository", ignoreCase = true)) {
                throw GitError.NotARepository(".")
            }
            throw GitError.CommandFailed(result.output.ifBlank { "git branch failed" })
        }

        result.output
            .lines()
            .filter { it.isNotBlank() }
            .map { line ->
                val current = line.startsWith("*")
                val name = line.removePrefix("*").trim()
                Branch(name = name, isCurrent = current)
            }
    }

    suspend fun switchBranch(name: String) = withContext(Dispatchers.Default) {
        val result = runner.run("git", "switch", name)
        if (!result.success) {
            throw GitError.CommandFailed(result.output.ifBlank { "git switch failed" })
        }
    }
}

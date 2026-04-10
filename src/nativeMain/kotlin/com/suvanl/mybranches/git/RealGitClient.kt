package com.suvanl.mybranches.git

import com.suvanl.mybranches.system.runCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealGitClient : GitClient {
  override suspend fun listBranches(prefix: String): List<Branch> = withContext(Dispatchers.Default) {
    val result = runCommand(
      "com/suvanl/mybranches/git",
      "branch",
      "--list",
      "$prefix/*",
      "--sort=-committerdate",
      "--format=%(HEAD)%(refname:short)",
    )
    if (!result.succeeded) {
      val output = result.output
      if (output.contains("not a git repository", ignoreCase = true)) {
        throw GitError.NotARepository(".")
      }
      throw GitError.CommandFailed(output.ifBlank { "git branch failed" })
    }
    result.output
      .lines()
      .filter { it.isNotBlank() }
      .map { line ->
        val current = line.startsWith("*")
        val name = line.removePrefix("*").removePrefix(" ").trim()
        Branch(name = name, current = current)
      }
  }

  override suspend fun switchBranch(name: String) = withContext(Dispatchers.Default) {
    val result = runCommand("com/suvanl/mybranches/git", "switch", name)
    if (!result.succeeded) {
      throw GitError.CommandFailed(result.output.ifBlank { "git switch failed" })
    }
  }
}

package git

sealed class GitError(message: String) : Exception(message) {
    class NotARepository(workingDir: String) :
        GitError("not a git repository: $workingDir")

    class CommandFailed(output: String) :
        GitError(output)
}

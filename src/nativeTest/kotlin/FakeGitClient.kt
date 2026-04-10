import git.Branch
import git.GitClient
import git.GitError

class FakeGitClient(
    private val branches: List<Branch> = emptyList(),
    private val switchError: GitError? = null,
    private val listError: GitError? = null,
) : GitClient {
    val switchedTo = mutableListOf<String>()

    override suspend fun listBranches(prefix: String): List<Branch> {
        listError?.let { throw it }
        return branches
    }

    override suspend fun switchBranch(name: String) {
        switchError?.let { throw it }
        switchedTo += name
    }
}

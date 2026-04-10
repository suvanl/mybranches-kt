import com.suvanl.mybranches.git.Branch
import com.suvanl.mybranches.git.GitClient
import com.suvanl.mybranches.git.GitError

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

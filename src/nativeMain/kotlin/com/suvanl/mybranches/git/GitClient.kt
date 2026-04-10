package com.suvanl.mybranches.git

data class Branch(
    val name: String,
    val isCurrent: Boolean,
)

interface GitClient {
    /**
     * Returns local branches matching [pattern], sorted by most recent commit first
     */
    suspend fun listBranches(pattern: String): List<Branch>

    /**
     * @throws GitError on failure
     */
    suspend fun switchBranch(name: String)
}

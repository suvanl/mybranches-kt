package com.suvanl.mybranches.ui

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.suvanl.mybranches.git.Branch

@Composable
fun BranchList(
  branches: List<Branch>,
  selected: Int,
  pageStart: Int,
  pageSize: Int,
) {
  val pageEnd = (pageStart + pageSize).coerceAtMost(branches.size)
  val visibleBranches = branches.subList(pageStart, pageEnd)

  Column {
    for ((offset, branch) in visibleBranches.withIndex()) {
      val index = pageStart + offset
      val cursor = if (index == selected) ">" else " "
      val currentMarker = if (branch.current) "* " else "  "
      Text("$cursor $currentMarker${branch.name}")
    }

    if (branches.size > pageSize) {
      Text("  ${selected + 1}/${branches.size}")
    }
  }
}

package com.suvanl.mybranches.ui

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.text.SpanStyle
import com.jakewharton.mosaic.text.buildAnnotatedString
import com.jakewharton.mosaic.text.withStyle
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.suvanl.mybranches.git.Branch

@Composable
fun BranchList(
    branches: List<Branch>,
    selected: Int,
    pageStart: Int,
    pageSize: Int,
    modifier: Modifier = Modifier,
) {
    val pageEnd = (pageStart + pageSize).coerceAtMost(branches.size)
    val visibleBranches = branches.subList(pageStart, pageEnd)

    Column(modifier = modifier) {
        if (branches.isEmpty()) {
            Text("  No matching branches")
        } else {
            for ((offset, branch) in visibleBranches.withIndex()) {
                val index = pageStart + offset
                val cursor = if (index == selected) ">" else " "

                val currentMarker = if (branch.isCurrent) "* " else "  "
                val branchNameColor = if (branch.isCurrent) Color.Green else Color.Unspecified

                val lineContent = buildAnnotatedString {
                    append("$cursor $currentMarker")
                    withStyle(SpanStyle(color = branchNameColor)) {
                        append(branch.name)
                    }
                }

                Text(lineContent)
            }

            if (branches.size > pageSize) {
                Text("  ${selected + 1}/${branches.size}")
            }
        }
    }
}

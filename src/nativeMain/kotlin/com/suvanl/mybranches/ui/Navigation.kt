package com.suvanl.mybranches.ui

/** Ensures the selected item is always within the visible page. */
internal fun visiblePageStart(
  selected: Int,
  pageStart: Int,
  pageSize: Int,
): Int = when {
  selected < pageStart -> selected
  selected >= pageStart + pageSize -> selected - pageSize + 1
  else -> pageStart
}.coerceAtLeast(0)

internal fun AppState.Ready.moveUp(): AppState.Ready {
  val newSelected = (selected - 1).coerceAtLeast(0)
  val newPageStart = if (newSelected < pageStart) newSelected else pageStart
  return copy(selected = newSelected, pageStart = newPageStart)
}

internal fun AppState.Ready.moveDown(pageSize: Int): AppState.Ready {
  val newSelected = (selected + 1).coerceAtMost(branches.size - 1)
  val pageEnd = pageStart + pageSize
  val newPageStart = if (newSelected >= pageEnd) pageStart + 1 else pageStart
  return copy(selected = newSelected, pageStart = newPageStart)
}

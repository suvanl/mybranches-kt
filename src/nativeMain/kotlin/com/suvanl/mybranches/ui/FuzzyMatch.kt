package com.suvanl.mybranches.ui

internal fun String.fuzzyContains(query: String): Boolean {
    var qi = 0
    for (ch in this) {
        if (qi < query.length && ch.lowercaseChar() == query[qi].lowercaseChar()) {
            qi++
        }
    }
    return qi == query.length
}

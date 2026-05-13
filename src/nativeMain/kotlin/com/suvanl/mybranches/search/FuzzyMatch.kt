package com.suvanl.mybranches.search

internal fun String.fuzzyContains(query: String): Boolean {
    val caseSensitive = query.any { it.isUpperCase() }
    var queryIndex = 0
    forEach { char ->
        if (queryIndex < query.length && char.matches(query[queryIndex], caseSensitive)) {
            queryIndex++
        }
    }
    return queryIndex == query.length
}

private fun Char.matches(other: Char, caseSensitive: Boolean): Boolean = if (caseSensitive) {
    this == other
} else {
    this.lowercaseChar() == other.lowercaseChar()
}

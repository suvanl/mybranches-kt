package com.suvanl.mybranches.ui

internal fun String.fuzzyContains(query: String): Boolean {
    var queryIndex = 0
    forEach { char ->
        if (queryIndex < query.length && char.lowercaseChar() == query[queryIndex].lowercaseChar()) {
            queryIndex++
        }
    }
    return queryIndex == query.length
}

package com.suvanl.mybranches.system

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
fun currentUsername(): String {
    val username = getenv("USER")?.toKString()
    check(!username.isNullOrBlank()) { $$"Could not determine username: $USER is not set" }
    return username
}

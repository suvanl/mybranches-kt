package com.suvanl.mybranches.system

fun interface CommandRunner {
    fun run(vararg args: String): CommandResult
}

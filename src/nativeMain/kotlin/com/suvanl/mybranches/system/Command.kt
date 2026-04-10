package com.suvanl.mybranches.system

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

private const val DEFAULT_BUFFER_SIZE = 4096

fun runCommand(vararg args: String): CommandResult {
    val escaped = args.joinToString(" ") { "'" + it.replace("'", "'\\''") + "'" }
    val fp = popen("$escaped 2>&1", "r") ?: return CommandResult("", false)

    val output = StringBuilder()
    memScoped {
        val buffer = allocArray<ByteVar>(length = DEFAULT_BUFFER_SIZE)
        while (fgets(buffer, DEFAULT_BUFFER_SIZE, fp) != null) {
            output.append(buffer.toKString())
        }
    }

    val status = pclose(fp)
    return CommandResult(output.toString().trimEnd(), status == 0)
}

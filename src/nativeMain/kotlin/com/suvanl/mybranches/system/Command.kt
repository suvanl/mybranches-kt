package com.suvanl.mybranches.system

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

private const val DEFAULT_BUFFER_SIZE = 4096

@OptIn(ExperimentalForeignApi::class)
fun runCommand(vararg args: String): CommandRunResult {
    val escaped = args.joinToString(" ") { arg ->
        "'" + arg.replace("'", "'\\''") + "'"
    }
    val fp = popen("$escaped 2>&1", "r") ?: return CommandRunResult("", false)

    val output = StringBuilder()
    memScoped {
        val buffer = allocArray<ByteVar>(length = DEFAULT_BUFFER_SIZE)
        while (fgets(buffer, DEFAULT_BUFFER_SIZE, fp) != null) {
            // buffer is `char*` so toString() would return something like `CPointer(raw=0x0)`
            output.append(buffer.toKString())
        }
    }

    val status = pclose(fp)
    return CommandRunResult(output.toString().trimEnd(), status == 0)
}

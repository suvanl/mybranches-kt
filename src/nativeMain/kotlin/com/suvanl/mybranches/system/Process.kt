@file:OptIn(ExperimentalForeignApi::class)

package com.suvanl.mybranches.system

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

data class CommandResult(
  val output: String,
  val succeeded: Boolean,
)

fun runCommand(vararg args: String): CommandResult {
  val escaped = args.joinToString(" ") { arg ->
    "'" + arg.replace("'", "'\\''") + "'"
  }
  val fp = popen("$escaped 2>&1", "r") ?: return CommandResult("", false)

  val output = StringBuilder()
  memScoped {
    val buffer = allocArray<ByteVar>(4096)
    while (fgets(buffer, 4096, fp) != null) {
      output.append(buffer.toKString())
    }
  }

  val status = pclose(fp)
  return CommandResult(output.toString().trimEnd(), status == 0)
}

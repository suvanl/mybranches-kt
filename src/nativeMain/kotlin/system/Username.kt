@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package system

import kotlinx.cinterop.toKString
import platform.posix.getenv

fun currentUsername(): String {
  val username = getenv("USER")?.toKString()
  check(!username.isNullOrBlank()) { "Could not determine username: \$USER is not set" }
  return username
}

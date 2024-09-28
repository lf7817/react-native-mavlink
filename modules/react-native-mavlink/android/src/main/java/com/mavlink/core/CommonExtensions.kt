package com.mavlink.core

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

suspend inline fun <T> retryWithDelay(
  times: Int,
  delay: Duration,
  block: () -> T
): T {
  repeat(times - 1) {
    try {
      return block()
    } catch (_: Exception) {
      coroutineContext.ensureActive()
      delay(delay)
    }
  }

  return block()
}

suspend inline fun <R> mavResultOf(block: () -> R): Result<R> = try {
  Result.success(block())
} catch (e: Throwable) {
  coroutineContext.ensureActive()
  when (e) {
    is TimeoutCancellationException -> Result.failure(Exception("Timeout"))
    is IOException -> Result.failure(Exception("Connection error"))
    is MavException -> Result.failure(e)
    else -> Result.failure(Exception("Error"))
  }
}
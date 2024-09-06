package com.mavlink

class MavException(
  message: String,
  cause: Throwable? = null
) : Exception(
  message,
  cause
)

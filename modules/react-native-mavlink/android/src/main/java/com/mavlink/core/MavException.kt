package com.mavlink.core

class MavException(
  message: String,
  cause: Throwable? = null
) : Exception(
  message,
  cause
)

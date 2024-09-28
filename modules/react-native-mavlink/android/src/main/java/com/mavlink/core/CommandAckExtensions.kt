package com.mavlink.core

import com.divpundir.mavlink.definitions.common.CommandAck
import com.divpundir.mavlink.definitions.common.MavResult

fun CommandAck.throwIfFailure() {
  when (this.result.entry) {

    MavResult.ACCEPTED -> Unit

    MavResult.IN_PROGRESS -> throw MavException("In progress")

    MavResult.TEMPORARILY_REJECTED -> throw MavException("Temporarily rejected")

    MavResult.DENIED -> throw MavException("Denied")

    MavResult.UNSUPPORTED -> throw MavException("Unsupported")

    MavResult.FAILED -> throw MavException("Failed")

    MavResult.COMMAND_LONG_ONLY -> throw MavException("Invalid command")

    MavResult.COMMAND_INT_ONLY -> throw MavException("Invalid command")

    MavResult.CANCELLED -> throw MavException("Cancelled")

    MavResult.COMMAND_UNSUPPORTED_MAV_FRAME -> throw MavException("Unsupported mav frame")
    
    null -> throw MavException("Unknown result")
  }
}
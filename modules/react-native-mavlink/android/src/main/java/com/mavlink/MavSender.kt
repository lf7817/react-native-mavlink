package com.mavlink

import com.divpundir.mavlink.api.MavMessage

interface MavSender {
  
  val systemId: UByte

  val componentId: UByte

  suspend fun <T : MavMessage<T>> send(message: T)
}
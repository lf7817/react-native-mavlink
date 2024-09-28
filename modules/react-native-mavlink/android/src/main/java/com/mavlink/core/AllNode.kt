package com.mavlink.core

import com.divpundir.mavlink.api.MavFrame
import com.divpundir.mavlink.api.MavMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AllNode(
  frames: Flow<MavFrame<out MavMessage<*>>>,
) : MavRemoteNode {

  override val systemId: UByte = 0u

  override val componentId: UByte = 0u

  override val message: Flow<MavMessage<*>> = frames.map { it.message }
}
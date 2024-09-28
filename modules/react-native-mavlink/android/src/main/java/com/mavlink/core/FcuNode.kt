package com.mavlink.core

import com.divpundir.mavlink.api.MavMessage
import com.divpundir.mavlink.api.MavFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

internal class FcuNode(
  mavFrame: Flow<MavFrame<out MavMessage<*>>>,
) : MavRemoteNode {

  override val systemId: UByte = 1u

  override val componentId: UByte = 1u

  override val message: Flow<MavMessage<*>> = mavFrame
    .filter { it.systemId == this.systemId && it.componentId == this.componentId }
    .map { it.message }
}
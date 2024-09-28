package com.mavlink.core

import com.divpundir.mavlink.api.MavMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

interface MavRemoteNode {

  val systemId: UByte

  val componentId: UByte
  
  val message: Flow<MavMessage<*>>
  
}

suspend inline fun <reified T : MavMessage<T>> MavRemoteNode.receive(): T = message
  .filterIsInstance<T>()
  .first()

suspend inline fun <reified T : MavMessage<T>> MavRemoteNode.receive(noinline predicate: (T) -> Boolean): T = message
  .filterIsInstance<T>()
  .first(predicate)
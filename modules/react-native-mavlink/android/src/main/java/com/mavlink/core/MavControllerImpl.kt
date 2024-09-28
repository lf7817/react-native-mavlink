package com.mavlink.core

import com.divpundir.mavlink.adapters.coroutines.CoroutinesMavConnection
import com.divpundir.mavlink.api.AbstractMavDialect
import com.divpundir.mavlink.api.MavMessage

internal class MavControllerImpl(
  override var systemId: UByte,
  override var componentId: UByte,
  override val dialect: AbstractMavDialect 
) : MavController {
  // 默认创建 UDP服务端
  override var connection: CoroutinesMavConnection by MavCoroutinesConnection(dialect)

  @Volatile
  override var fcu: MavRemoteNode = FcuNode(
    mavFrame = connection.mavFrame
  )

  @Volatile
  override var all: MavRemoteNode = AllNode(
    frames = connection.mavFrame
  )

  override suspend fun <T : MavMessage<T>> send(message: T) {
    connection.sendUnsignedV2(systemId, componentId, message)
  }
}

fun MavController(
  systemId: UByte,
  componentId: UByte,
  dialect: AbstractMavDialect
): MavController = MavControllerImpl(
  systemId = systemId,
  componentId = componentId,
  dialect = dialect
)
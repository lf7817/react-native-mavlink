package com.mavlink.service

import com.divpundir.mavlink.api.MavEnumValue
import com.divpundir.mavlink.api.contains
import com.divpundir.mavlink.definitions.common.Attitude
import com.divpundir.mavlink.definitions.common.MavCmd
import com.divpundir.mavlink.definitions.common.MavDataStream
import com.divpundir.mavlink.definitions.common.RequestDataStream
import com.divpundir.mavlink.definitions.minimal.Heartbeat
import com.divpundir.mavlink.definitions.minimal.MavModeFlag
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mavlink.core.MavUtils
import com.mavlink.core.MavController
import com.mavlink.core.sendCommandLong
import com.mavlink.core.throwIfFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout

class TelemetryService(private val mavController: MavController, reactContext: ReactApplicationContext): MavService(reactContext) {
  
  val armed: Flow<Boolean> = mavController.fcu.message
    .filterIsInstance<Heartbeat>()
    .map { it.baseMode.contains(MavModeFlag.SAFETY_ARMED) }

  suspend fun requestDataStreams() {
    requestDataStream(MavDataStream.RAW_SENSORS, 2u) 
    requestDataStream(MavDataStream.EXTENDED_STATUS, 2u)
    requestDataStream(MavDataStream.RC_CHANNELS, 2u)
    requestDataStream(MavDataStream.POSITION, 3u)
    requestDataStream(MavDataStream.EXTRA1, 10u)
    requestDataStream(MavDataStream.EXTRA2, 10u)
    requestDataStream(MavDataStream.EXTRA3, 3u)
  }
  
  private suspend fun requestDataStream(stream: MavDataStream, rate: UShort) {
    mavController.send(
      RequestDataStream(
        targetSystem = mavController.fcu.systemId,
        targetComponent = mavController.all.componentId,
        startStop = 1u,
        reqStreamId = stream.value.toUByte(),
        reqMessageRate = rate
      )
    )
  }
  
  fun changeArmState(state: Boolean, promise: Promise) = MavUtils.promiseResult(promise) {
    withTimeout(3000) {
      mavController.sendCommandLong(
        command = MavEnumValue.of(MavCmd.COMPONENT_ARM_DISARM),
        confirmation = 1u,
        param1 = if (state) 1f else 0f,
        param2 = 0f,
      ).throwIfFailure()
    }
  }

  suspend fun subscribeFrame() {
    mavController.fcu.message.collect {
      when(it) {
        is Heartbeat,
        is Attitude -> {
          sendEvent(
            "message",
            "{\"type\": \"${it::class.simpleName}\", \"data\": ${MavUtils.toJson(it)}}"
          )
        }
      }
    }
  }
}
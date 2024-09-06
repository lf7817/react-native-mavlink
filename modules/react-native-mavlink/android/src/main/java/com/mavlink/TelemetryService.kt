package com.mavlink

import android.util.Log
import com.divpundir.mavlink.api.MavEnumValue
import com.divpundir.mavlink.api.MavMessage
import com.divpundir.mavlink.api.contains
import com.divpundir.mavlink.definitions.common.Attitude
import com.divpundir.mavlink.definitions.common.MavCmd
import com.divpundir.mavlink.definitions.common.MavDataStream
import com.divpundir.mavlink.definitions.common.RequestDataStream
import com.divpundir.mavlink.definitions.minimal.Heartbeat
import com.divpundir.mavlink.definitions.minimal.MavModeFlag
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout

class TelemetryService(private val mavController: MavController) {
  private val duration = 3000L
  
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
  
  suspend fun changeArmState(state: Boolean) = withTimeout(duration) {
    mavController.sendCommandLong(
      command = MavEnumValue.of(MavCmd.COMPONENT_ARM_DISARM),
      confirmation = 1u,
      param1 = if (state) 1f else 0f,
      param2 = 0f,
    ).throwIfFailure()
  }
  
  suspend fun mavFrames(callback: (String, WritableMap) -> Unit) {
    mavController.fcu.message.collect {
      val result = generateMsg(it)

      if (result!= null) callback("message", result)
    }
  }

  private fun generateMsg(message: MavMessage<*>): WritableMap? {
    val params = Arguments.createMap()
    val data = Arguments.createMap()

    return when (message) {
      is Attitude -> {
        params.putString("type", Attitude::class.java.simpleName)
        data.putString("roll", message.roll.toString())
        data.putString("pitch", message.pitch.toString())
        data.putString("yaw", message.yaw.toString())
        data.putString("rollspeed", message.rollspeed.toString())
        data.putString("pitchspeed", message.pitchspeed.toString())
        data.putString("yawspeed", message.yawspeed.toString())
        data.putString("timeBootMs", message.timeBootMs.toString())
        params.putMap("data", data)
        params
      }

      else -> null
    }
  }
}
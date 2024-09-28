package com.mavlink.core

import android.util.Log
import com.divpundir.mavlink.adapters.coroutines.CoroutinesMavConnection
import com.divpundir.mavlink.adapters.coroutines.asCoroutine
import com.divpundir.mavlink.api.AbstractMavDialect
import com.divpundir.mavlink.api.MavEnumValue
import com.divpundir.mavlink.api.wrap
import com.divpundir.mavlink.connection.tcp.TcpClientMavConnection
import com.divpundir.mavlink.connection.tcp.TcpServerMavConnection
import com.divpundir.mavlink.connection.udp.UdpClientMavConnection
import com.divpundir.mavlink.connection.udp.UdpServerMavConnection
import com.divpundir.mavlink.definitions.common.CommandAck
import com.divpundir.mavlink.definitions.common.CommandInt
import com.divpundir.mavlink.definitions.common.CommandLong
import com.divpundir.mavlink.definitions.common.MavCmd
import com.divpundir.mavlink.definitions.common.MavParamType
import com.divpundir.mavlink.definitions.common.ParamRequestRead
import com.divpundir.mavlink.definitions.common.ParamSet
import com.divpundir.mavlink.definitions.common.ParamValue
import com.divpundir.mavlink.definitions.common.MavFrame
import com.divpundir.mavlink.definitions.common.ParamRequestList
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

interface MavController : MavSender {
  val dialect: AbstractMavDialect
  
  var connection: CoroutinesMavConnection
  
  var fcu: MavRemoteNode

  var all: MavRemoteNode
}

fun MavController.createUdpServer(port: Int)  {
  connection = UdpServerMavConnection(port, dialect).asCoroutine()
  fcu = FcuNode(connection.mavFrame)
  all = AllNode(connection.mavFrame)
  Log.d("Mavlink", "UDP: Created server on port $port")
}

fun MavController.createUdpClient(ip: String, port: Int) {
  connection = UdpClientMavConnection(ip, port, dialect).asCoroutine()
  fcu = FcuNode(connection.mavFrame)
  all = AllNode(connection.mavFrame)
  Log.d("Mavlink", "Udp: create connection to $ip:$port")
}

fun MavController.createTcpServer(port: Int) {
  connection = TcpServerMavConnection(port, dialect).asCoroutine()
  fcu = FcuNode(connection.mavFrame)
  all = AllNode(connection.mavFrame)
  Log.d("Mavlink", "TCP: Created server on port $port")
}

fun MavController.createTcpClient(ip: String, port: Int) {
  connection = TcpClientMavConnection(ip, port, dialect).asCoroutine()
  fcu = FcuNode(connection.mavFrame)
  all = AllNode(connection.mavFrame)
  Log.d("Mavlink", "TCP: create connection to $ip:$port")
}

fun MavController.createSerial(path: String, burateRate: Int) {
  connection = SerialMavConnection(path, burateRate, dialect).asCoroutine()
  fcu = FcuNode(connection.mavFrame)
  all = AllNode(connection.mavFrame)
  Log.d("Mavlink", "Serial: Connected to $path:$burateRate")
}

suspend fun MavController.getParam(id: String): Float = coroutineScope {
  val ack = async { fcu.receive<ParamValue> { it.paramId == id } }
  delay(10)

  send(
    ParamRequestRead(
      targetSystem = fcu.systemId,
      targetComponent = fcu.componentId,
      paramId = id,
      paramIndex = -1
    )
  )

  ack.await().paramValue
}

/**
 * 获取参数列表
 * @desc 发送ParamRequestList请求，等待返回参数列表
 */
suspend fun MavController.getParamList(send: ((String, Int) -> Unit)?) = coroutineScope {
  val params = mutableListOf<ParamValue>()
  var percent = 0
  
  val job = launch {
    fcu.message.filterIsInstance<ParamValue>().collect {
      params.add(it)
      
      val nPercent = params.size * 100 / it.paramCount.toInt()

      // 减少发送频率
      if (percent != nPercent) {
        percent = nPercent
        send?.invoke("param:list:progress", percent)
      }

      if (params.size == it.paramCount.toInt()) {
        cancel()
      }
    }
  }
  
  delay(10)

  send(
    ParamRequestList(
      targetSystem = fcu.systemId,
      targetComponent = fcu.componentId,
    )
  )

  job.join()
  params
}

suspend fun MavController.setParam(id: String, value: Float) : Float = coroutineScope {
  val ack = async { fcu.receive<ParamValue> { it.paramId == id } }
  delay(10)

  send(
    ParamSet(
      targetSystem = fcu.systemId,
      targetComponent = fcu.componentId,
      paramId = id,
      paramValue = value,
      paramType = MavParamType.REAL32.wrap()
    )
  )

  ack.await().paramValue
}

suspend fun MavController.sendCommandLong(
  command: MavEnumValue<MavCmd>,
  confirmation: UByte = 0u,
  param1: Float = 0F,
  param2: Float = 0F,
  param3: Float = 0F,
  param4: Float = 0F,
  param5: Float = 0F,
  param6: Float = 0F,
  param7: Float = 0F,
): CommandAck = coroutineScope {

  val ack = async { fcu.receive<CommandAck> { it.command == command } }
  delay(10)

  send(
    CommandLong(
      targetSystem = fcu.systemId,
      targetComponent = fcu.componentId,
      command = command,
      confirmation = confirmation,
      param1 = param1,
      param2 = param2,
      param3 = param3,
      param4 = param4,
      param5 = param5,
      param6 = param6,
      param7 = param7
    )
  )

  ack.await()
}

suspend fun MavController.sendCommandInt(
  frame: MavEnumValue<MavFrame>,
  command: MavEnumValue<MavCmd>,
  x: Int,
  y: Int,
  z: Float,
  current: UByte = 0u,
  autocontinue: UByte = 0u,
  param1: Float = 0F,
  param2: Float = 0F,
  param3: Float = 0F,
  param4: Float = 0F,
): CommandAck = coroutineScope {

  val ack = async { fcu.receive<CommandAck> { it.command == command } }
  delay(10)

  send(
    CommandInt(
      targetSystem = fcu.systemId,
      targetComponent = fcu.componentId,
      frame = frame,
      command = command,
      current = current,
      autocontinue = autocontinue,
      param1 = param1,
      param2 = param2,
      param3 = param3,
      param4 = param4,
      x = x,
      y = y,
      z = z
    )
  )

  ack.await()
}
package com.mavlink.service

import android.util.Log
import com.divpundir.mavlink.adapters.coroutines.tryConnect
import com.divpundir.mavlink.connection.StreamState
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mavlink.core.MavUtils
import com.mavlink.core.MavController
import com.mavlink.core.createSerial
import com.mavlink.core.createTcpClient
import com.mavlink.core.createTcpServer
import com.mavlink.core.createUdpClient
import com.mavlink.core.createUdpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class ConnectionService(private val mavController: MavController, reactContext: ReactApplicationContext): MavService(reactContext), KoinComponent {
  private val telemetryService: TelemetryService by inject()
  private var connectJob: Job? = null
  private var frameJob: Job? = null
  
  private fun cleanJob() {
    frameJob?.cancel()
    connectJob?.cancel()
  }

  fun createUdpServer(port: Int, promise: Promise) = MavUtils.promiseResult(promise) {
    cleanJob()
    mavController.createUdpServer(port)
  }

  fun createUdpClient(ip: String, port: Int, promise: Promise) = MavUtils.promiseResult(promise) {
    cleanJob()
    mavController.createUdpClient(ip, port)
  }
  
  fun createTcpServer(port: Int, promise: Promise) = MavUtils.promiseResult(promise) {
    cleanJob()
    mavController.createTcpServer(port)
  }

  fun createTcpClient(ip: String, port: Int, promise: Promise) = MavUtils.promiseResult(promise) {
    cleanJob()
    mavController.createTcpClient(ip, port)
  }

  fun createSerial(path: String, baudRate: Int, promise: Promise) =
    MavUtils.promiseResult(promise) {
      cleanJob()
      mavController.createSerial(path, baudRate)
    }

  fun disconnect(promise: Promise) = MavUtils.promiseResult(promise) {
    mavController.connection.close()
    cleanJob()
  }

  fun connect(promise: Promise) {
    if (connectJob?.isActive == true) {
      promise.resolve(true)
    } else {
      Log.d("Mavlink","连接Rover中...")
      connectJob = CoroutineScope(Dispatchers.IO).launch {
        launch {
          mavController.connection.streamState.collect {
            when (it) {
              is StreamState.Inactive.Failed -> {
                sendEvent("connection:status", "Failed")
                Log.d("Mavlink","意外断开连接，原因: ${it.cause}")
                Log.d("Mavlink","尝试重新连接中...")
                while (!mavController.connection.tryConnect(this)) {
                  delay(1000)
                }
                Log.d("Mavlink","重新连接成功！")
              }
              is StreamState.Inactive.Stopped -> {
                sendEvent("connection:status", "Stopped")
                Log.d("Mavlink","连接处于关闭状态，可能还没连接过或者用户主动关闭了连接")
              }
              is StreamState.Active -> {
                frameJob?.cancel()
                sendEvent("connection:status", "Active")
                promise.resolve(true)
                frameJob = launch { telemetryService.subscribeFrame() }
                Log.d("Mavlink","已连接到Rover!")
              }
              else -> Unit
            }
          }
        }
        delay(10)
        while (!mavController.connection.tryConnect(this@launch)) {
          delay(1000)
        }
      }
    }
  }

  fun getSerialPortList(promise: Promise) = MavUtils.promiseResult(promise) {
    Arguments.createArray().apply {
      val devDirectory = File("/dev")

      if (devDirectory.exists() && devDirectory.isDirectory) {
        val files = devDirectory.listFiles()
        if (files != null) {
          for (file in files) {
            if (file.isDirectory) {
              continue
            }
            val fileName = file.name
            if (fileName.startsWith("ttyS") || fileName.startsWith("ttyH") || fileName.startsWith("ttyUSB")) {
              pushString("/dev/$fileName")
            }
          }
        }
      }
    }
  }
}
package com.mavlink

import android.util.Log
import com.divpundir.mavlink.adapters.coroutines.tryClose
import com.divpundir.mavlink.adapters.coroutines.tryConnect
import com.divpundir.mavlink.connection.StreamState
import com.divpundir.mavlink.definitions.common.CommonDialect
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MavlinkModule (reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    private var mavFrameJob:Job? = null
    private val mavController: MavController = MavController(
      systemId = 255u,
      componentId = 1u,
      CommonDialect
    )
    private val telemetryService = TelemetryService(mavController)

    companion object {
      const val NAME = "Mavlink"
    }
  
    override fun getName(): String {
      return NAME
    }
  
    fun promiseResult(promise: Promise, callback: suspend () -> Unit) = CoroutineScope(Dispatchers.IO).launch{
      mavResultOf { callback() }
        .onSuccess { promise.resolve(null) }
        .onFailure { promise.resolve(it.message) }
    }

     fun <T> sendEvent(eventName: String, params: T?) {
        reactApplicationContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit(eventName, params)
     }
  
    @ReactMethod
    fun createUdpServer(port: Int, promise: Promise) = CoroutineScope(Dispatchers.IO).launch {
      try {
        mavFrameJob?.cancel()
        mavController.createUdpServer(port)
        promise.resolve(null)
      } catch (e: Exception) {
        promise.resolve(e.message)
      }
    }

    @ReactMethod
    fun createUdpClient(ip: String, port: Int, promise: Promise) = CoroutineScope(Dispatchers.IO).launch {
      try {
        mavFrameJob?.cancel()
        mavController.createUdpClient(ip, port)
        promise.resolve(null)
      }  catch (e: Exception) {
        promise.resolve(e.message)
      }
    }
  
    @ReactMethod
    fun createSerial(path: String, burateRate: Int, promise: Promise) = CoroutineScope(Dispatchers.IO).launch {
      try {
        mavFrameJob?.cancel()
        mavController.createSerial(path, burateRate)
        promise.resolve(null)
      } catch (e: Exception) {
        promise.resolve(e.message)
      }
    }
  
    @ReactMethod
    fun disconnect(promise: Promise) = CoroutineScope(Dispatchers.IO).launch {
      mavFrameJob?.cancel()
      promise.resolve(mavController.connection.tryClose())
    }
    
    @ReactMethod
    fun connect(promise: Promise) {
      if (mavFrameJob?.isActive == true) {
        return
      }
      
      mavFrameJob = CoroutineScope(Dispatchers.IO).launch {
        Log.d("Mavlink","Connecting to FCU...")
        while (!mavController.connection.tryConnect(this@launch)) {
          delay(1000)
        }
        Log.d("Mavlink","Connected to FCU")
        promise.resolve(true)
        
        launch {
          Log.d("Mavlink","监听遥测数据")
          telemetryService.mavFrames(::sendEvent)
        }

        launch {
          mavController
            .connection
            .streamState
            .filterIsInstance<StreamState.Inactive.Failed>()
            .collect {
              Log.d("Mavlink","Connection failed: ${it.cause}")
              sendEvent("connectionFailed", it.cause.toString())
            }
        }
      }
    }
    
    /**
     * 锁定/解释
     */
    
    @ReactMethod
    fun changeArmState(state: Boolean, promise: Promise) = promiseResult(promise) {
      telemetryService.changeArmState(state)
    }
  
    @ReactMethod
    fun getParam(id: String, promise: Promise) = CoroutineScope(Dispatchers.IO).launch {
      val value = withTimeoutOrNull(4000) {
        mavController.getParam(id)
      }
      Log.d("Mavlink", "param $id value is $value")
      promise.resolve(value)
    }
}
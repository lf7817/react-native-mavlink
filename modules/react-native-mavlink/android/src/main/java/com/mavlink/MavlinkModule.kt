package com.mavlink

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.mavlink.service.ConnectionService
import com.mavlink.service.ParameterService
import com.mavlink.service.TelemetryService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MavlinkModule (reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), KoinComponent {
    private val connectionService: ConnectionService by inject()
    private val telemetryService: TelemetryService by inject()
    private val parameterService: ParameterService by inject()
  
    companion object {
      const val NAME = "Mavlink"
    }

    override fun getName(): String {
      return NAME
    }
  
    override fun getConstants(): MutableMap<String, Any> =
      hashMapOf("DEFAULT_EVENT_NAME" to "New Event")

    @ReactMethod
    fun createUdpServer(port: Int, promise: Promise) = 
      connectionService.createUdpServer(port, promise)

    @ReactMethod
    fun createUdpClient(ip: String, port: Int, promise: Promise) = 
      connectionService.createUdpClient(ip, port, promise)
    
    @ReactMethod
    fun createTcpServer(port: Int, promise: Promise) = 
      connectionService.createTcpServer(port, promise)

    @ReactMethod
    fun createTcpClient(ip: String, port: Int, promise: Promise) = 
      connectionService.createTcpClient(ip, port, promise)
  
    @ReactMethod
    fun createSerial(path: String, baudRate: Int, promise: Promise) = 
      connectionService.createSerial(path, baudRate, promise)
  
    @ReactMethod
    fun disconnect(promise: Promise) =
      connectionService.disconnect(promise)
  
    @ReactMethod
    fun connect(promise: Promise) = 
      connectionService.connect(promise)
  
    /**
     * 锁定/解锁
     */
    @ReactMethod
    fun changeArmState(state: Boolean, promise: Promise) = 
      telemetryService.changeArmState(state, promise)
  
    /**
     * 获取参数
     */
    @ReactMethod
    fun getParam(id: String, promise: Promise) = 
      parameterService.getParam(id, promise)
  
    /**
     * 设置参数
     */
    @ReactMethod
    fun setParam(id: String, value: Float, promise: Promise) = 
      parameterService.setParam(id, value, promise)
  
    /**
     * 获取参数列表
     */
    @ReactMethod
    fun getParamList(promise: Promise) = 
      parameterService.getParamList(promise)

    /**
     * 获取串口列表
     */
    @ReactMethod
    fun getSerialPortList(promise: Promise) = connectionService.getSerialPortList(promise)
}
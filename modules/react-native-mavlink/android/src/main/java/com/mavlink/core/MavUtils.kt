package com.mavlink.core

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MavUtils {
  private val gson = GsonBuilder()
    .serializeSpecialFloatingPointValues()
    .create()

  /**
   * 将对象转为json字符串
   */
  public fun toJson(obj: Any): String {
    return gson.toJson(obj)
      .replace("NaN", "null")
      .replace("\"instanceCompanion\":{},", "")
  }

  /**
   * 发送事件
   */
  public fun sendEvent(reactContext: ReactApplicationContext, eventName: String, params: Any?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }


  public fun promiseResult(promise: Promise, callback: suspend () -> Any?) = CoroutineScope(Dispatchers.IO).launch {
    mavResultOf { callback() }
      .onSuccess { 
        promise.resolve(
          when(it) {
            is Unit -> null
            else -> it
          }
        )
      }
      .onFailure { 
        promise.reject(it)
      }
  }
  
    // 太耗时了 ，废弃
//  public fun mavMessageToWritableMap(obj: MavMessage<*>): WritableMap {
//    val map = Arguments.createMap()
//    val data = Arguments.createMap()
//    val kClass = obj::class
//    val properties = kClass.declaredMemberProperties
//    map.putString("type", kClass.simpleName)
//
//    properties.forEach {
//      if (it.name == "instanceCompanion") return@forEach
//
//      when(val value = it.getter.call(obj)) {
//        is String -> data.putString(it.name, value)
//        is Float -> data.putDouble(it.name, value.toDouble())
//        is Double -> data.putDouble(it.name, value)
//        is UInt -> data.putInt(it.name, value.toInt())
//        is Int -> data.putInt(it.name, value)
//        is Boolean -> data.putBoolean(it.name, value)
//        is ULong -> data.putLong(it.name, value.toLong())
//        is Long -> data.putLong(it.name, value)
//        is UShort -> data.putInt(it.name, value.toInt())
//        is Short -> data.putInt(it.name, value.toInt())
//        is Byte -> data.putInt(it.name, value.toInt())
//        is UByte -> data.putInt(it.name, value.toInt())
//        is List<*> -> data.putArray(it.name, Arguments.fromArray(value))
//
//        else -> data.putNull(it.name)
//      }
//    }
//
//    map.putMap("data", data)
//    return map
//  }
}
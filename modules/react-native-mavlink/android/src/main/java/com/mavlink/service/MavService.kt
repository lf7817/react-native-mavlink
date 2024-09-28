package com.mavlink.service

import com.facebook.react.bridge.ReactApplicationContext
import com.mavlink.core.MavUtils

open class MavService(private val reactContext: ReactApplicationContext) {
  
  public fun sendEvent(eventName: String, params: Any?) =
    MavUtils.sendEvent(reactContext, eventName, params)
}
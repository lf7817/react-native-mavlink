package com.mavlink.service

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class RNEventEmitterService(private val reactContext: ReactApplicationContext) {
    /**
     * 发送事件
     */
    public fun sendEvent(eventName: String, params: Any?) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }
}
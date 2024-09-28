package com.mavlink.service

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.mavlink.core.MavUtils
import com.mavlink.core.MavController
import com.mavlink.core.getParam
import com.mavlink.core.getParamList
import com.mavlink.core.setParam
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ParameterService(reactContext: ReactApplicationContext): MavService(reactContext), KoinComponent {
  private val mavController: MavController by inject()

  /**
   * Get parameter by id
   */
  public fun getParam(id: String, promise: Promise) = MavUtils.promiseResult(promise) {
    withTimeout(3000) {
      mavController.getParam(id)
    }
  }

  public fun setParam(id: String, value: Float ,promise: Promise) =
    MavUtils.promiseResult(promise) {
      withTimeout(3000) {
        mavController.setParam(id, value)
      }
    }

  /**
   * Get all parameters
   */
  public fun getParamList(promise: Promise) = MavUtils.promiseResult(promise) {
    withTimeout(60000) {
      MavUtils.toJson(mavController.getParamList(::sendEvent))
    }
  }
}
package com.mavlink

import com.divpundir.mavlink.api.wrap
import com.divpundir.mavlink.definitions.common.GpsFixType
import com.divpundir.mavlink.definitions.common.GpsRawInt
import com.divpundir.mavlink.definitions.common.MavCmd
import kotlinx.coroutines.withTimeout

class CalibrationService(
  private val mavController: MavController
) {
  suspend fun calibrateMagnetometerSimple(): Unit = withTimeout(5000) {
    val gpsRawInt = mavController.fcu.receive<GpsRawInt>()

    when (gpsRawInt.fixType.entry) {
      GpsFixType.NO_GPS, GpsFixType.NO_FIX,
      GpsFixType._2D_FIX, GpsFixType.STATIC,
      GpsFixType.PPP, null -> throw MavException("GPS lock required")

      GpsFixType._3D_FIX, GpsFixType.DGPS,
      GpsFixType.RTK_FLOAT, GpsFixType.RTK_FIXED -> Unit
    }

    mavController
      .sendCommandLong(
        command = MavCmd.FIXED_MAG_CAL_YAW.wrap(),
      )
      .throwIfFailure()
  }
}
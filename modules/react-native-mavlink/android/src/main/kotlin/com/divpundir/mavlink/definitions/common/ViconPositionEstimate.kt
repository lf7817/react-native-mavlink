package com.divpundir.mavlink.definitions.common

import com.divpundir.mavlink.api.GeneratedMavField
import com.divpundir.mavlink.api.GeneratedMavMessage
import com.divpundir.mavlink.api.MavMessage
import com.divpundir.mavlink.serialization.MavDataDecoder
import com.divpundir.mavlink.serialization.MavDataEncoder
import com.divpundir.mavlink.serialization.encodeFloat
import com.divpundir.mavlink.serialization.encodeFloatArray
import com.divpundir.mavlink.serialization.encodeUInt64
import com.divpundir.mavlink.serialization.safeDecodeFloat
import com.divpundir.mavlink.serialization.safeDecodeFloatArray
import com.divpundir.mavlink.serialization.safeDecodeUInt64
import com.divpundir.mavlink.serialization.truncateZeros
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Float
import kotlin.Int
import kotlin.UInt
import kotlin.ULong
import kotlin.Unit
import kotlin.collections.List

/**
 * Global position estimate from a Vicon motion system source.
 *
 * @param usec Timestamp (UNIX time or time since system boot)
 * units = us
 * @param x Global X position
 * units = m
 * @param y Global Y position
 * units = m
 * @param z Global Z position
 * units = m
 * @param roll Roll angle
 * units = rad
 * @param pitch Pitch angle
 * units = rad
 * @param yaw Yaw angle
 * units = rad
 * @param covariance Row-major representation of 6x6 pose cross-covariance matrix upper right
 * triangle (states: x, y, z, roll, pitch, yaw; first six entries are the first ROW, next five entries
 * are the second ROW, etc.). If unknown, assign NaN value to first element in the array.
 */
@GeneratedMavMessage(
  id = 104u,
  crcExtra = 56,
)
public data class ViconPositionEstimate(
  /**
   * Timestamp (UNIX time or time since system boot)
   * units = us
   */
  @GeneratedMavField(type = "uint64_t")
  public val usec: ULong = 0uL,
  /**
   * Global X position
   * units = m
   */
  @GeneratedMavField(type = "float")
  public val x: Float = 0F,
  /**
   * Global Y position
   * units = m
   */
  @GeneratedMavField(type = "float")
  public val y: Float = 0F,
  /**
   * Global Z position
   * units = m
   */
  @GeneratedMavField(type = "float")
  public val z: Float = 0F,
  /**
   * Roll angle
   * units = rad
   */
  @GeneratedMavField(type = "float")
  public val roll: Float = 0F,
  /**
   * Pitch angle
   * units = rad
   */
  @GeneratedMavField(type = "float")
  public val pitch: Float = 0F,
  /**
   * Yaw angle
   * units = rad
   */
  @GeneratedMavField(type = "float")
  public val yaw: Float = 0F,
  /**
   * Row-major representation of 6x6 pose cross-covariance matrix upper right triangle (states: x,
   * y, z, roll, pitch, yaw; first six entries are the first ROW, next five entries are the second ROW,
   * etc.). If unknown, assign NaN value to first element in the array.
   */
  @GeneratedMavField(
    type = "float[21]",
    extension = true,
  )
  public val covariance: List<Float> = emptyList(),
) : MavMessage<ViconPositionEstimate> {
  override val instanceCompanion: MavMessage.MavCompanion<ViconPositionEstimate> = Companion

  override fun serializeV1(): ByteArray {
    val encoder = MavDataEncoder(SIZE_V1)
    encoder.encodeUInt64(usec)
    encoder.encodeFloat(x)
    encoder.encodeFloat(y)
    encoder.encodeFloat(z)
    encoder.encodeFloat(roll)
    encoder.encodeFloat(pitch)
    encoder.encodeFloat(yaw)
    return encoder.bytes
  }

  override fun serializeV2(): ByteArray {
    val encoder = MavDataEncoder(SIZE_V2)
    encoder.encodeUInt64(usec)
    encoder.encodeFloat(x)
    encoder.encodeFloat(y)
    encoder.encodeFloat(z)
    encoder.encodeFloat(roll)
    encoder.encodeFloat(pitch)
    encoder.encodeFloat(yaw)
    encoder.encodeFloatArray(covariance, 84)
    return encoder.bytes.truncateZeros()
  }

  public companion object : MavMessage.MavCompanion<ViconPositionEstimate> {
    private const val SIZE_V1: Int = 32

    private const val SIZE_V2: Int = 116

    override val id: UInt = 104u

    override val crcExtra: Byte = 56

    override fun deserialize(bytes: ByteArray): ViconPositionEstimate {
      val decoder = MavDataDecoder(bytes)

      val usec = decoder.safeDecodeUInt64()
      val x = decoder.safeDecodeFloat()
      val y = decoder.safeDecodeFloat()
      val z = decoder.safeDecodeFloat()
      val roll = decoder.safeDecodeFloat()
      val pitch = decoder.safeDecodeFloat()
      val yaw = decoder.safeDecodeFloat()
      val covariance = decoder.safeDecodeFloatArray(84)

      return ViconPositionEstimate(
        usec = usec,
        x = x,
        y = y,
        z = z,
        roll = roll,
        pitch = pitch,
        yaw = yaw,
        covariance = covariance,
      )
    }

    public operator fun invoke(builderAction: Builder.() -> Unit): ViconPositionEstimate =
        Builder().apply(builderAction).build()
  }

  public class Builder {
    public var usec: ULong = 0uL

    public var x: Float = 0F

    public var y: Float = 0F

    public var z: Float = 0F

    public var roll: Float = 0F

    public var pitch: Float = 0F

    public var yaw: Float = 0F

    public var covariance: List<Float> = emptyList()

    public fun build(): ViconPositionEstimate = ViconPositionEstimate(
      usec = usec,
      x = x,
      y = y,
      z = z,
      roll = roll,
      pitch = pitch,
      yaw = yaw,
      covariance = covariance,
    )
  }
}

package com.mavlink.core

import android.serialport.SerialPort
import com.divpundir.mavlink.api.MavDialect
import com.divpundir.mavlink.connection.AbstractMavConnection
import com.divpundir.mavlink.connection.BufferedMavConnection
import com.divpundir.mavlink.connection.MavConnection
import okio.Closeable
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import okio.IOException

class SerialMavConnection(
  private val path: String,
  private val baudRate: Int,
  private val dialect: MavDialect
): AbstractMavConnection() {

  @Volatile
  private lateinit var serialPort: SerialPort

  inner class Resource(private val instance: SerialPort): Closeable {
    override fun close() {
      instance.tryClose()
    }
  }

  @Throws(IOException::class)
  override fun open(): MavConnection {
    serialPort = SerialPort(File(path), baudRate)

    return BufferedMavConnection(
      source = serialPort.inputStream.source().buffer(),
      sink = serialPort.outputStream.sink().buffer(),
      resource = Resource(serialPort),
      dialect = dialect
    )
  }

  @Throws(IOException::class)
  override fun interruptOpen() {
    super.interruptOpen()
    serialPort?.tryClose()
  }
}
# react-native mavlink教程

本教程介绍了在 react-native中使用 mavlink构建地面站 APP的方法。为了方便复用，采用[Local library](https://reactnative.dev/docs/local-library-setup)封装了 ``react-native-mavlink``模块（由于每个公司都会有自己的[XML Definition Files &amp; Dialects](https://mavlink.io/zh/messages/)，所以无法封装通用包发布到npm ）

支持串口、UDP/TCP服务端、UDP/TCP客户端等多种通讯方式，支持随意切换

## 生成 MAVLink 库文件

将 definitions放到 ``modules/react-native-mavlink/definitions``目录下，然后再 ``build.gradle``中添加下面任务

```gradle
tasks.generateMavlink {
  include(file("definitions/minimal.xml"))
  include(file("definitions/standard.xml"))
  include(file("definitions/common.xml"))

  generatedSourcesDir = file("src/main/kotlin")
}
```

执行 ``gradle generateMavlink``即可生成代码到指定目录

## 无人机容器

身边没有设备的可以使用[px4-gazebo-headless](https://github.com/JonasVautherin/px4-gazebo-headless)这个容器

```bash
docker run --rm -it jonasvautherin/px4-gazebo-headless:1.14.3 192.168.0.12 # 填写手机 IP地址
```

在手机端使用 UDP监听 14540端口即可收到数据

## 串口

本代码基于[divyanshupundir/mavlink-kotlin](https://github.com/divyanshupundir/mavlink-kotlin)实现，不支持串口，但是扩展性很好，简单改改即可实现串口通讯（这里要注意下，不是 USB串口）

```kotlin
package com.mavlink

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
```

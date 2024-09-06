# react-native mavlink教程

本教程介绍了在 react-native中使用 mavlink构建地面站 APP的方法。为了方便复用，采用[Local librarie](https://reactnative.dev/docs/local-library-setup)封装了 ``react-native-mavlink``模块（由于每个公司都会有自己的[XML Definition Files &amp; Dialects](https://mavlink.io/zh/messages/)，所以无法封装通用包发布到npm ）

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

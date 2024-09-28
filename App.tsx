/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  Button,
  View,
} from 'react-native';
import { MavlinkModule } from 'react-native-mavlink';

MavlinkModule.onMessage('message', (message: any) => {
  console.log(message);
});

MavlinkModule.onMessage('param:list:progress', (message: any) => {
  console.log('param:list:progress', message);
});

MavlinkModule.onMessage('connection:status', (message: any) => {
  console.log('connection:status', message);
});


export default function App() {
  return (
    <View>
      <Button title="创建连接：UDP服务端（14540）" onPress={() => MavlinkModule.createUdpServer(14540)} />
      <Button title="创建连接：UDP客户端" onPress={() => MavlinkModule.createUdpClient('192.168.144.12', 19856)} />
      <Button
        title="创建连接：TCP服务端"
        onPress={() => {
          MavlinkModule.createTcpServer(8888);
        }}
      />

      <Button
        title="创建连接：串口（/dev/ttyHS0:115200）"
        onPress={() => {
          MavlinkModule.createSerial('/dev/ttyHS0', 115200);
        }}
      />

      <Button
        title="连接"
        onPress={() => {
          MavlinkModule.connect();
        }}
      />

      <Button
        title="断开连接"
        onPress={async () => {
          console.log(await MavlinkModule.disconnect());
        }}
      />

      <Button title="参数列表" onPress={async () => {
        const { res, err } = await MavlinkModule.getParamList();
        console.log(res, err);
      }} />

      <Button title="读取参数: SPRAY_SPINNER" onPress={async () => {
        const { res, err } = await MavlinkModule.getParam('SPRAY_SPINNER');
        console.log(res, typeof res, err, err?.message, err?.name);
      }} />

      <Button title="设置参数: SPRAY_SPINNER" onPress={async () => {
        const { res, err } = await MavlinkModule.setParam('SPRAY_SPINNER', Math.random() * 1000);
        console.log(res, typeof res, err, err?.message, err?.name);
      }} />

      <Button
        title="串口列表"
        onPress={async () => {
          const { res } = await MavlinkModule.getSerialPortList();
          console.log(res);
        }}
      />
      <Button title="上锁" onPress={() => MavlinkModule.arm()} />
      <Button title="解锁" onPress={() => MavlinkModule.disArm()} />
    </View>
  );
}

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

const mavlinkModule = new MavlinkModule();

mavlinkModule.onMessage((message) => {
  console.log('message', message);
});


export default function App() {
  return (
    <View>
      <Button title="create serial connection" onPress={() => mavlinkModule.createSerial('/dev/ttyHS0', 115200)} />
      <Button title="create udp service connection" onPress={() => mavlinkModule.createUdpServer(14550)} />
      <Button title="create udp client connection" onPress={() => mavlinkModule.createUdpClient('192.168.144.12', 19856)} />
      <Button title="connect" onPress={() => mavlinkModule.connect()} />
      <Button title="disconnect" onPress={() => mavlinkModule.disconnect()} />
      <Button title="arm" onPress={() => mavlinkModule.arm()} />
      <Button title="disarm" onPress={() => mavlinkModule.disArm()} />
      <Button title="get param" onPress={async () => {
        console.log('param', await mavlinkModule.getParam('PARAM_NAME'));
      }} />
    </View>
  );
}

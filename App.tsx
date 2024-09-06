/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  Text,
  View,
} from 'react-native';
import { multiply } from 'react-native-mavlink';

multiply(3, 7).then((result) => console.log(result));

export default function App() {
  return (
    <View>
      <Text>hello:</Text>
    </View>
  );
}

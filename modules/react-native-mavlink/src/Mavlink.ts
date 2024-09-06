import {NativeModules, Platform} from 'react-native';

const LINKING_ERROR =
  "The package 'react-native-mavlink' doesn't seem to be linked. Make sure: \n\n" +
  Platform.select({ios: "- You have run 'pod install'\n", default: ''}) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export const Mavlink = NativeModules.Mavlink
  ? NativeModules.Mavlink
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      },
    );

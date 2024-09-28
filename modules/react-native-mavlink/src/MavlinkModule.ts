import {NativeEventEmitter} from 'react-native'
import {Mavlink} from './Mavlink'
import {transformResult} from './utils'
import {ParamValue} from './types'

class MavlinkModuleCore extends NativeEventEmitter {
  private constructor() {
    super(Mavlink)
  }

  private static instance: MavlinkModuleCore

  public static getInstance(): MavlinkModuleCore {
    if (!MavlinkModuleCore.instance) {
      MavlinkModuleCore.instance = new MavlinkModuleCore()
    }
    return MavlinkModuleCore.instance
  }

  public disconnect = async () => transformResult<void>(Mavlink.disconnect())

  public connect = async () => transformResult<boolean>(Mavlink.connect())

  public arm = async () => transformResult<void>(Mavlink.changeArmState(true))

  public disArm = async () =>
    transformResult<void>(Mavlink.changeArmState(false))

  public onMessage = (name: string, callback: (message: any) => void) => {
    return this.addListener(name, callback).remove
  }

  public createUdpClient = async (ip: string, port: number) =>
    transformResult<void>(Mavlink.createUdpClient(ip, port))

  public createUdpServer = async (port: number) =>
    transformResult<void>(Mavlink.createUdpServer(port))

  public createTcpClient = async (ip: string, port: number) =>
    transformResult<void>(Mavlink.createTcpClient(ip, port))

  public createTcpServer = async (port: number) =>
    transformResult<void>(Mavlink.createTcpServer(port))

  public createSerial = async (path: string, burateRate: number) =>
    transformResult<void>(Mavlink.createSerial(path, burateRate))

  public setParam = async (id: string, value: number) =>
    transformResult<number>(Mavlink.setParam(id, value))

  public getParam = async (id: string) =>
    transformResult<number>(Mavlink.getParam(id))

  public getParamList = async () =>
    transformResult<Array<ParamValue>>(Mavlink.getParamList(), JSON.parse)

  public getSerialPortList = async () =>
    transformResult<Array<string>>(Mavlink.getSerialPortList())
}

export const MavlinkModule = MavlinkModuleCore.getInstance()

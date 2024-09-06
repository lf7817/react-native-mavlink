import {NativeEventEmitter} from 'react-native'
import {Mavlink} from './Mavlink'

export class MavlinkModule {
  private eventEmitter: NativeEventEmitter

  constructor() {
    this.eventEmitter = new NativeEventEmitter(Mavlink)
  }

  public disconnect = async () => Mavlink.disconnect()
  public connect = async () => Mavlink.connect()
  public arm = async () => Mavlink.changeArmState(true)
  public disArm = async () => {
    console.log('error', await Mavlink.changeArmState(false))
  }

  public onMessage = (callback: (message: any) => void) => {
    this.eventEmitter.removeAllListeners('message')
    this.eventEmitter.addListener('message', callback)
  }

  public createUdpClient = async (ip: string, port: number) =>
    Mavlink.createUdpClient(ip, port)

  public createUdpServer = async (port: number) => Mavlink.createUdpServer(port)

  public createSerial = async (path: string, burateRate: number) =>
    Mavlink.createSerial(path, burateRate)

  public getParam = async (id: string) => Mavlink.getParam(id)
}

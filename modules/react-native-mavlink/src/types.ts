export type Result<T> = {res?: T; err: undefined} | {err: Error; res: undefined}

export type MavEnumValue = {
  entry: string
  value: number
}

export type ParamValue = {
  /**
   * 参数索引, 通过getParamList接口可正确获取，但是通过getParam接口获取时，该值为 -1
   */
  paramIndex: number
  /**
   * 参数数量
   */
  paramCount: number
  /**
   * 参数 id
   */
  paramId: string
  /**
   * 参数类型
   */
  paramType: MavEnumValue
  /**
   * 参数值
   */
  paramValue: number
}

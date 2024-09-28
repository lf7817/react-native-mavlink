import {Result} from './types'

export async function transformResult<T>(
  promise: Promise<T>,
  formatter?: (data: any) => T,
): Promise<Result<T>> {
  try {
    const res = await promise
    return {
      res: typeof formatter === 'function' ? formatter(res) : res,
      err: undefined,
    }
  } catch (e: any) {
    return {res: undefined, err: e}
  }
}

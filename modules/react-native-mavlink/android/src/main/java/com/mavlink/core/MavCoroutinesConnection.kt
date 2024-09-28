package com.mavlink.core

import android.util.Log
import com.divpundir.mavlink.adapters.coroutines.CoroutinesMavConnection
import com.divpundir.mavlink.adapters.coroutines.asCoroutine
import com.divpundir.mavlink.api.AbstractMavDialect
import com.divpundir.mavlink.connection.udp.UdpServerMavConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

class MavCoroutinesConnection(dialect: AbstractMavDialect) {
  private var _connection: CoroutinesMavConnection = UdpServerMavConnection(14550, dialect).asCoroutine()
    
  operator fun getValue(thisref: Any, property: KProperty<*>) = _connection
  
  operator fun setValue(thisref: Any, property: KProperty<*>, value: CoroutinesMavConnection) {
    val old = _connection

    CoroutineScope(Dispatchers.IO).launch {
      try {
        old.close()
        Log.d("Mavlink", "旧连接已关闭")
      } catch (e: Exception) {
        Log.d("Mavlink", "旧连接关闭异常：$e")
      }
    }

    _connection = value
  }
}
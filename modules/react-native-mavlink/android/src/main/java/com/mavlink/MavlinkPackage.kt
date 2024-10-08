package com.mavlink

import com.divpundir.mavlink.definitions.common.CommonDialect
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.mavlink.core.MavController
import com.mavlink.service.ConnectionService
import com.mavlink.service.ParameterService
import com.mavlink.service.RNEventEmitterService
import com.mavlink.service.TelemetryService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class MavlinkPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    // 开发时 reload会导致createNativeModules多次调用，导致 koin 重复初始化报错
    if (GlobalContext.getKoinApplicationOrNull() != null) {
      GlobalContext.stopKoin()
    }

    val appModule = module {
      single { MavController(255u, 1u, CommonDialect) }
      single { RNEventEmitterService(reactContext) }
      singleOf(::ConnectionService)
      singleOf(::TelemetryService)
      singleOf(::ParameterService)
    }

    GlobalContext.startKoin {
      androidContext(reactContext)
      androidLogger()
      modules(appModule)
    }
    
    return listOf(MavlinkModule(reactContext))
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return emptyList()
  }
}

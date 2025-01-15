package jp.juggler.v2mixup

import android.app.Application
import jp.juggler.v2mixup.di.app.AppComponent
import jp.juggler.v2mixup.di.app.DaggerAppComponent
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class BaseApplication : Application() {

  private lateinit var component: AppComponent

  override fun onCreate() {
    super.onCreate()

    component =
      DaggerAppComponent
        .builder()
        .setMainDispatcher(Main)
        .setDefaultDispatcher(Default)
        .setIoDispatcher(IO)
        .setApplication(this)
        .build()
  }

  fun getAppComponent(): AppComponent {
    return component
  }
}
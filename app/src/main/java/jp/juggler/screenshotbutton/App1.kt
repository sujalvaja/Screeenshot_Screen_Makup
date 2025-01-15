package jp.juggler.screenshotbutton

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import jp.juggler.util.LogCategory
import jp.juggler.v2mixup.di.app.AppComponent
import jp.juggler.v2mixup.di.app.DaggerAppComponent
import kotlinx.coroutines.Dispatchers

class App1 : Application() {
    private lateinit var component: AppComponent
    companion object {
        const val tagPrefix = "ScreenShotButton"

        lateinit var pref: SharedPreferences

        private var isPrepared = false

        fun prepareAppState(contextArg: Context) {
            if (!isPrepared) {
                val context = contextArg.applicationContext
                isPrepared = true
                pref = Pref.pref(context)
                LogCategory.onInitialize(context)
                Capture.onInitialize(context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        prepareAppState(applicationContext)

        component =
            DaggerAppComponent
                .builder()
                .setMainDispatcher(Dispatchers.Main)
                .setDefaultDispatcher(Dispatchers.Default)
                .setIoDispatcher(Dispatchers.IO)
                .setApplication(this)
                .build()
    }
    fun getAppComponent(): AppComponent {
        return component
    }

}

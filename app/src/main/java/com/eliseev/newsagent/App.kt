package com.eliseev.newsagent

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.eliseev.newsagent.di.DaggerAppComponent
import com.eliseev.newsagent.di.Injector

@Suppress("unused")
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        Injector.component = DaggerAppComponent.builder().build()
        super.attachBaseContext(base)

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls()
                    .detectNetwork()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build())
        }
    }
}
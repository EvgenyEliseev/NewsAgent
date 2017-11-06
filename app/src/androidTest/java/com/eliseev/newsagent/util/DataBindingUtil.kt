package com.eliseev.newsagent.util

import android.support.test.InstrumentationRegistry
import android.view.Choreographer
import java.util.concurrent.CountDownLatch

/**
 * Bindings are executed on main thread on the next Choreographer frame
 */
fun waitForPendingBindings() {
    val latch = CountDownLatch(1)
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        Choreographer.getInstance().postFrameCallback { latch.countDown() }
    }
    latch.await()
}

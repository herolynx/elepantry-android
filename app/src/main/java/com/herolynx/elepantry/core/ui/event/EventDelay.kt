package com.herolynx.elepantry.core.ui.event

import org.joda.time.Duration
import java.util.*

class EventDelay(
        private val delayTime: Duration = Duration.standardSeconds(1)
) {

    private var timer: Timer? = null

    fun execute(action: () -> Unit) {
        if (timer != null) {
            timer?.cancel()
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                action()
            }
        }, delayTime.millis)
    }

}
package com.herolynx.elepantry.ext.dropbox.auth

import android.app.Activity
import android.os.SystemClock
import com.dropbox.core.android.Auth
import com.herolynx.elepantry.R
import com.herolynx.elepantry.auth.Token
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.getAuthContext
import org.joda.time.Duration
import rx.Observable

object DropBoxAuth {

    private val TAG = "[DropBox][Auth]"

    fun getToken(
            a: Activity,
            waitTime: Duration = Duration.millis(50),
            maxWaitTime: Duration = Duration.standardSeconds(30)
    ): Observable<Token> {
        val dropBoxToken = Auth.getOAuth2Token()
        debug("${TAG} Checking auth token: $dropBoxToken")
        if (dropBoxToken != null) {
            return Observable.just(dropBoxToken)
        } else {
            return logIn(a, waitTime, maxWaitTime)
        }
    }

    private fun logIn(a: Activity, waitTime: Duration, maxWaitTime: Duration): Observable<Token> = Observable.defer {
        debug("${TAG} Logging in")
        Auth.startOAuth2Authentication(a, a.getString(R.string.dropbox_app_key))
        var dropBoxToken: Token? = null
        var time = Duration.ZERO
        while (dropBoxToken == null && time.isShorterThan(maxWaitTime)) {
            dropBoxToken = Auth.getOAuth2Token()
            time = time.plus(waitTime)
            SystemClock.sleep(waitTime.millis)
        }
        debug("${TAG} Log-in finished - token: $dropBoxToken")
        if (dropBoxToken != null) {
            a.getAuthContext().map { c -> c.setDropBoxAccount(dropBoxToken!!) }
            Observable.just(dropBoxToken)
        } else {
            Observable.error(RuntimeException("DropBox login failed due to missing token - login could be cancelled by the user"))
        }
    }


}
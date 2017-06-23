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

    fun getSession(
            a: Activity,
            waitTime: Duration = Duration.millis(50),
            maxWaitTime: Duration = Duration.standardSeconds(30)
    ): Observable<DropBoxSession> {
        val dropBoxToken = Auth.getOAuth2Token()
        debug("${TAG} Checking auth token: $dropBoxToken")
        if (dropBoxToken != null) {
            return Observable.just(DropBoxSession(dropBoxToken, Auth.getUid()))
        } else {
            return logIn(a, waitTime, maxWaitTime)
        }
    }

    private fun logIn(a: Activity, waitTime: Duration, maxWaitTime: Duration): Observable<DropBoxSession> = Observable.defer {
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
            val session = DropBoxSession(dropBoxToken, Auth.getUid())
            a.getAuthContext().map { c -> c.setDropBoxAccount(session) }
            Observable.just(session)
        } else {
            Observable.error(RuntimeException("DropBox login failed due to missing token - login could be cancelled by the user"))
        }
    }


}
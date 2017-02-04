package com.herolynx.elepantry.ext.google.firebase

import android.os.SystemClock
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import rx.Observable

object FirebaseAuth {

    fun logIn(account: GoogleSignInAccount): Observable<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val task = FirebaseAuth.getInstance()
                .signInWithCredential(credential)

        return Observable.defer {
            while (!task.isComplete) {
                SystemClock.sleep(100)
            }
            if (task.isSuccessful) Observable.just(task.result!!)
            else Observable.error(task.exception!!)
        }
    }

}
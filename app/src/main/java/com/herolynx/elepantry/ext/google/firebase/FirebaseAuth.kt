package com.herolynx.elepantry.ext.google.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.herolynx.elepantry.ext.google.generic.toObservable
import rx.Observable

object FirebaseAuth {

    fun logIn(account: GoogleSignInAccount): Observable<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .toObservable()
    }

}
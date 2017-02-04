package com.herolynx.elepantry.ext.google.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.herolynx.elepantry.ext.google.generic.toObservable
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import rx.Observable

object FirebaseAuth {

    fun getCurrentUser(): Try<FirebaseUser> {
        return FirebaseAuth.getInstance()
                .currentUser
                .toOption()
                .map { u -> Try.Success(u) }
                .getOrElse { Try.Failure(RuntimeException("User not logged in")) }
    }

    fun logIn(account: GoogleSignInAccount): Observable<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        return FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .toObservable()
    }

}
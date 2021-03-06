package com.herolynx.elepantry.ext.google.firebase.auth

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

    fun logIn(account: GoogleSignInAccount): Observable<Pair<GoogleSignInAccount?, AuthResult>> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return FirebaseAuth.getInstance().signInWithCredential(credential)
                .toObservable()
                .map { authResult -> Pair(account, authResult) }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

}
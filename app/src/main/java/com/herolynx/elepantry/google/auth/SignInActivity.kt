package com.herolynx.elepantry.google.auth

import com.herolynx.elepantry.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SignInActivity : AppCompatActivity(),
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */
                )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        findViewById(R.id.sign_in_button)?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        throw UnsupportedOperationException()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException()
    }
}

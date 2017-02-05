package com.herolynx.elepantry.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.core.view.WithProgressDialog
import com.herolynx.elepantry.core.view.toast
import com.herolynx.elepantry.ext.google.auth.GoogleAuth
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.ext.google.firebase.FirebaseAuth
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class SignInActivity : AppCompatActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private var api: GoogleApiClient? = null

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ signIn() })
    }

    private fun redirectLoggedInUser() {
        FirebaseAuth.getCurrentUser()
                .onSuccess { u ->
                    info("User already logged in - id: %s", u.uid)
//                    navigateTo(MainActivity::class.java)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViewListeners()
        redirectLoggedInUser()
        api = GoogleAuth.build(this, { connectionResult ->
            error("[GoogleApi] Connection failed: %s", connectionResult)
        })
//        api?.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
//            override fun onConnected(var1: Bundle?) {
//                info("[GoogleApi] onConnected")
//            }
//
//            override fun onConnectionSuspended(var1: Int) {
//                info("[GoogleApi] onConnectionSuspended")
//            }
//        })
//        api.connect()
//        info("[GoogleAPI] after connect " + api.isConnecting + ", " + api.isConnected)
//        var i =0
//        while(i<10) {
//            SystemClock.sleep(2000)
//            info("[GoogleAPI] after connect " + api.isConnecting + ", " + api.isConnected)
//            i++
//        }
//        api.stopAutoManage(this)
//        api.disconnect()
//
//        GoogleAuth.silentLogIn(this, { connectionResult -> error("[GoogleApi][SilentSignIn] Connection failed: %s", connectionResult) })
//                .filter { r -> r.isDefined() }
//                .map { r -> r.get() }
//                .map { account ->
//                    debug("[Google][SilentSignIn] Account: %s", account.idToken)
//                }

//       GoogleDrive.create(this)
//               .search()


//        debug("[Google] IsConnected: %s", api.isConnected)
//        GoogleDrive(api)
//                .search()
    }

    override fun onRestart() {
        super.onRestart()
        redirectLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        redirectLoggedInUser()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            GoogleAuth.onLogInResult(data)
                    .onFailure { ex ->
                        error("[Google] Couldn't log in user", ex)
                        toast(R.string.auth_failed, "Google Account")
                    }
                    .onSuccess { account ->
                        val credential = GoogleAccountCredential.usingOAuth2(this,
                                Collections.singleton("https://www.googleapis.com/auth/drive.readonly")
                        )
                        credential.setSelectedAccount(account.account)
                        val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
                        val JSON_FACTORY = JacksonFactory.getDefaultInstance()
                        val service = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build()
//                        Thread() {
//                             fun run() {
//                                 val size = service.files().list().execute().files.size
//                                 debug("[GoogleDrive] Files: " + size)
//                            }
//                        }.start()


                        //                        api?.connect()
//                        var i = 0
//                        while (i < 10) {
//                            SystemClock.sleep(200)
//                            info("[GoogleAPI] after connect " + api?.isConnecting + ", " + api?.isConnected)
//                            i++
//                        }
                        GoogleDrive(api!!).search()
                        debug("[Firebase] Logging in - account id: %s", account.id)
                        showProgressDialog(this)
                        FirebaseAuth.logIn(account)
                                .subscribeOn(Schedulers.io())
                                .map { auth ->
                                    service.files().list().execute().files.forEach { f ->
                                        debug("[GoogleDrive] File: " + f.name)
                                    }

                                    auth
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { auth ->
                                            hideProgressDialog()
//                                            navigateTo(MainActivity::class.java)
                                        },
                                        { ex ->
                                            error("[Firebase] Couldn't log in user", ex)
                                            toast(R.string.auth_failed, "Firebase")
                                        }
                                )
                    }
        }
    }

    private fun signIn() {
        val signInIntent = GoogleAuth.logIn(api!!, this, { connectionResult ->
            error("[Google] Connection failed: %s", connectionResult)
            toast(R.string.auth_failed, "Google Account")
        })
        startActivityForResult(signInIntent, Intents.GOOGLE_SIGN_IN)
    }

}

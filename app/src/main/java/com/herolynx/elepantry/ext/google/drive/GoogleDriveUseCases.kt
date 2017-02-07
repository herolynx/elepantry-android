package com.herolynx.elepantry.ext.google.drive

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.AppContext
import com.herolynx.elepantry.core.func.toObservable
import com.herolynx.elepantry.core.log.debug
import org.funktionale.option.Option
import org.funktionale.option.toOption
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

object GoogleDriveUseCases {

    fun search(
            ctx: Option<AppContext>,
            googleDriveFactory: (GoogleSignInAccount) -> GoogleDrive,
            text: String = ""
    ) {
        ctx
                .flatMap { a -> a.getMainAccount().toOption() }
                .map { acc -> googleDriveFactory(acc) }
                .toObservable()
                .flatMap { gDrive -> gDrive.search(text) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    debug("[File] Name: " + f.name)
                }
    }

}
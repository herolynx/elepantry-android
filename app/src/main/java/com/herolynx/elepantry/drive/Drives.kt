package com.herolynx.elepantry.drive

import android.app.Activity
import com.herolynx.elepantry.ext.dropbox.auth.DropBoxAuth
import com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.getAuthContext
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import rx.Observable

object Drives {

    fun isOnline(a: Activity, type: DriveType): Boolean = when (type) {

        DriveType.GOOGLE_DRIVE -> true

        DriveType.DROP_BOX -> a.getAuthContext()
                .flatMap { c -> c.dropBoxSession.toOption() }
                .isDefined()

        else -> throw UnsupportedOperationException("Unsupported drive: $type")
    }

    fun login(a: Activity, type: DriveType): Observable<CloudDrive> = when (type) {

        DriveType.GOOGLE_DRIVE -> Observable.just(drive(a, type))

        DriveType.DROP_BOX -> DropBoxAuth.getSession(a).map { s -> drive(a, type) }

        else -> throw UnsupportedOperationException("Unsupported drive: $type")
    }

    fun drives(a: Activity): List<CloudDrive> = DriveType.values()
            .map { type -> drive(a, type) }
            .toList()

    fun drive(a: Activity, type: DriveType): CloudDrive = when (type) {

        DriveType.GOOGLE_DRIVE -> GoogleDrive.create(a).get()

        DriveType.DROP_BOX -> DropBoxDrive.create(a)
                .getOrElse { NotConnectedDrive(DriveType.DROP_BOX) }

        else -> throw UnsupportedOperationException("Unsupported drive: $type")

    }

}
package com.herolynx.elepantry.drive

import android.app.Activity
import com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive
import com.herolynx.elepantry.ext.google.drive.GoogleDrive

object Drives {

    fun drives(a: Activity): List<CloudDrive> = DriveType.values()
            .map { type -> drive(a, type) }
            .toList()

    fun drive(a: Activity, type: DriveType): CloudDrive = when (type) {

        DriveType.GOOGLE_DRIVE -> GoogleDrive.create(a).get()

        DriveType.DROP_BOX -> DropBoxDrive.create(a).toBlocking().first()

        else -> throw UnsupportedOperationException("Unsupported drive: $type")

    }

}
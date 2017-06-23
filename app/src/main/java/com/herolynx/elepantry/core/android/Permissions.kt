package com.herolynx.elepantry.core.android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Environment
import android.support.v4.app.ActivityCompat
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.notification.toast

object Permissions {

    val RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 20001

    fun assertExternalStoragePermission(a: Activity) {
        if (!Environment.getExternalStorageDirectory().canWrite() && !Environment.getExternalStorageDirectory().canRead()) {
            a.toast(R.string.per_no_ext_storage_access)
            throw RuntimeException("Access not granted to external storage")
        }
    }

    fun requestWriteExternalStoragePermission(a: Activity) {
        val reqPermission: () -> Unit = {
            ActivityCompat.requestPermissions(
                    a,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    RC_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(a, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(a)
                    .setTitle(a.getString(R.string.per_ext_storage_title))
                    .setMessage(a.getString(R.string.per_ext_storage_desc))
                    .setPositiveButton(R.string.ok) {
                        dialog,
                        which ->
                        reqPermission()
                    }
                    .show()
        } else {
            reqPermission()
        }
    }

}
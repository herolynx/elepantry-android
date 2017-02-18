package com.herolynx.elepantry.core.ui.notification

import android.app.ProgressDialog
import android.content.Context
import com.herolynx.elepantry.R

interface WithProgressDialog {

    var mProgressDialog: ProgressDialog?

    fun showProgressDialog(c: Context) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(c)
            mProgressDialog?.setMessage(c.getString(R.string.loading))
            mProgressDialog?.isIndeterminate = true
        }
        mProgressDialog?.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog?.isShowing ?: false) {
            mProgressDialog?.dismiss()
        }
    }

}

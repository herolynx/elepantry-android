package com.herolynx.elepantry.core

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import com.herolynx.elepantry.R

open class BaseActivity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null

    protected fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.setMessage(getString(R.string.loading))
            mProgressDialog?.isIndeterminate = true
        }
        mProgressDialog?.show()
    }

    protected fun hideProgressDialog() {
        if (mProgressDialog?.isShowing ?: false) {
            mProgressDialog?.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

}

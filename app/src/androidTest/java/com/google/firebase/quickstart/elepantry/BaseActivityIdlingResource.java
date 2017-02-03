package com.google.firebase.quickstart.elepantry;

import android.app.ProgressDialog;
import android.support.test.espresso.IdlingResource;

import com.herolynx.elepantry.BaseActivity;

/**
 * Monitor Activity idle status by watching ProgressDialog.
 */
public class BaseActivityIdlingResource implements IdlingResource {

    private BaseActivity mActivity;
    private ResourceCallback mCallback;

    public BaseActivityIdlingResource(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public String getName() {
        return "BaseActivityIdlingResource:" + mActivity.getLocalClassName();
    }

    @Override
    public boolean isIdleNow() {
        ProgressDialog dialog = mActivity.getMProgressDialog();
        boolean idle = (dialog == null || !dialog.isShowing());

        if (mCallback != null && idle) {
            mCallback.onTransitionToIdle();
        }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }
}

package com.zhx.householdapp.data

import android.hardware.Camera

class Model {
    private var mCamera: Camera? = null

    fun beginCamera(): Camera? {
        if (mCamera == null) {
            mCamera = Camera.open()
            return mCamera
        }
        return mCamera
    }

    fun reCamera(): Camera? {
        mCamera = Camera.open()
        return mCamera
    }
}
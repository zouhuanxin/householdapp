package com.zhx.householdapp.activity.autolight

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zhx.householdapp.R
import com.zhx.householdapp.app.MyApplication
import com.zhx.householdapp.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_auto_light.*


class AutoLightActivity: AppCompatActivity() {
    private var mainViewModel: AutoLightViewModel? = null
    private var mCamera: Camera? = null
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_light)
        mainViewModel = ViewModelProvider(this).get(AutoLightViewModel::class.java)
        PermissionUtils().verifyStoragePermissions(
            this,
            object : PermissionUtils.PermissionCallBack {
                override fun success() {

                }
            })
        initView()
        initData()
    }

    fun initView() {

    }

    fun initData() {
        initCamera()
    }

    fun initCamera() {
        camreaview.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                try {
                    if (mCamera != null){
                        mCamera!!.stopPreview()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mCamera = MyApplication.model!!.beginCamera()
                mCamera!!.setDisplayOrientation(90)
                mCamera!!.setPreviewTexture(surface)
                //mCamera!!.setPreviewDisplay(surfaceCreated)
                mCamera!!.startPreview()
               // bindService(Intent(this@AutoLightActivity, SnapService::class.java), mConnection, BIND_AUTO_CREATE)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mCamera!!.setPreviewCallback(null)
                mCamera!!.stopPreview()
                mCamera!!.release()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }
    }

    override fun onPause() {
        super.onPause()
        if (mCamera != null) {
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            listOf(mCamera!!)
            mCamera!!.release()
        }
    }
}
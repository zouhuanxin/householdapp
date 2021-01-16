package com.zhx.householdapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zhx.householdapp.R
import com.zhx.householdapp.activity.autolight.AutoLightActivity
import com.zhx.householdapp.activity.autolight.AutoLightViewModel
import com.zhx.householdapp.activity.smartassistant.SmartAssistantActivity
import com.zhx.householdapp.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionUtils().verifyStoragePermissions(
            this,
            object : PermissionUtils.PermissionCallBack {
                override fun success() {

                }
            })
        initView()
    }

    fun initView() {
        auto_light.setOnClickListener(this)
        smart_assistant.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.auto_light -> {
                //自动灯光
                startActivity(Intent(this,AutoLightActivity::class.java))
            }
            R.id.smart_assistant -> {
                //智能助手
                startActivity(Intent(this,SmartAssistantActivity::class.java))
            }
        }
    }

}
package com.zhx.householdapp.service

import android.app.Service
import android.content.Intent
import android.hardware.Camera
import android.media.FaceDetector
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import com.dhh.websocket.RxWebSocket
import com.dhh.websocket.WebSocketSubscriber
import com.zhx.householdapp.activity.smartassistant.SmartAssistantActivity
import com.zhx.householdapp.app.MyApplication
import com.zhx.householdapp.util.Base64Utils
import com.zhx.householdapp.util.TimeUtil
import okhttp3.Call
import okhttp3.WebSocket
import org.json.JSONObject
import rx.schedulers.Schedulers
import zhx.hello.usbweightv1.http.Apis
import zhx.hello.usbweightv1.http.HttpCallBack
import zhx.hello.usbweightv1.http.OkHttpUtil
import java.io.IOException
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class SmartAssistantService : Service() {
    private var mSpeech: TextToSpeech? = null
    private var camera: Camera? = null
    private val url = "ws://49.234.123.245:8082/my_SXD_Socket?account=one"

    //控制并发
    private var indexDate: Date = Date()

    //缓存 上下100条分析
    private val caches: ArrayList<Int> = ArrayList()

    //上一个灯光状态
    //-1 关灯
    // 1 开灯
    private var LightStatus: Int = -1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        mSpeech = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {

            }
        })
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        mSpeech!!.setPitch(2.0f)
        // 设置语速
        mSpeech!!.setSpeechRate(1.0f)
        RxWebSocket.get(url)
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : WebSocketSubscriber() {
                override fun onMessage(text: String) {
                    println("接到信息:" + text)
                    val rep = JSONObject(text)
                    if (rep.getJSONObject("msg")
                            .toString().length > 100 && rep.getJSONObject("msg")
                            .getInt("person_num") > 0
                    ) {
                        addCache(1)
                        //判断是否在房间内，如果在就不播放了
                        if (LightStatus == -1 && !mSpeech!!.isSpeaking) {
                            LightStatus = 1
                            mSpeech!!.speak(
                                SmartAssistantActivity.weatherInfo,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                "speech"
                            )
                        }
                    } else {
                        addCache(-1)
                    }
                }

                override fun onOpen(webSocket: WebSocket) {
                    super.onOpen(webSocket)
                    Task()
                }

                override fun onReconnect() {
                    super.onReconnect()
                    println("重新链接")
                }
            })
    }

    //开启抓拍任务
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Task() {
        camera = MyApplication.model!!.beginCamera()
        if (camera != null) {
            Thread(Runnable {
                run {
                    while (true){
                        camera!!.autoFocus(null)
                    }
                }
            }).start()
            camera!!.setPreviewCallback(object : Camera.PreviewCallback {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                    if (data != null && TimeUtil.subtractionTime(indexDate) > 500) {
                        indexDate = Date()
                        var previewSize = camera!!.getParameters().getPreviewSize()
                        var raw = Base64Utils.ByteToImage(data, previewSize.width, previewSize.height)
                        val imagedata = Base64Utils.ByteToBase64(raw)
                        send(imagedata)
                    }
                }
            })
        }
    }

    /**
     * 发送信息
     */
    fun send(imagedata: String) {
        val msg = JSONObject()
        msg.put("msg", imagedata)
        msg.put("type", -1)
        msg.put("to", "one")
        RxWebSocket.send(
            url,
            msg.toString()
        )
    }

    /**
     * 添加缓存
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun addCache(status: Int) {
        if (status == -1 && IsOutRoom() && LightStatus == 1 && !mSpeech!!.isSpeaking) {
            //判断是否离开屋子关闭灯光
            LightStatus = -1
            mSpeech!!.speak(
                "一定要多喝水多信息哦。",
                TextToSpeech.QUEUE_ADD,
                null,
                "speech"
            )
        }
        if (caches.size > 100) {
            caches.add(caches.size - 1, status)
            caches.remove(0)
        } else {
            caches.add(status)
        }
    }

    /**
     * 是否离开房间
     * 当前最后一条记录往前加载5条如果全部都是-1则表示离开房间，无人在房间中
     */
    fun IsOutRoom(): Boolean {
        if (caches.size < 6) {
            return false
        }
        var index = 0
        for (i in caches.size - 6..caches.size - 1) {
            if (caches.get(i) == -1) {
                index++
            }
        }
        if (index == 5) {
            return true
        } else {
            return false
        }
    }

}
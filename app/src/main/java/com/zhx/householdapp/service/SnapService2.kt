package com.zhx.householdapp.service

import android.app.Service
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import com.zhx.householdapp.app.MyApplication
import com.zhx.householdapp.util.Base64Utils
import com.zhx.householdapp.util.TimeUtil
import okhttp3.Call
import org.json.JSONObject
import zhx.hello.usbweightv1.http.Apis
import zhx.hello.usbweightv1.http.HttpCallBack
import zhx.hello.usbweightv1.http.OkHttpUtil
import java.io.IOException
import java.util.*

@Deprecated("")
class SnapService2 : Service() {
    private var mSpeech: TextToSpeech? = null
    private var camera: Camera? = null

    //控制帧数
    private var indexDate: Date = Date()

    //图像数据
    private var data: ByteArray? = null

    //唯一id
    private val case_id = Random().nextInt().toString()

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
        mSpeech!!.setSpeechRate(0.5f)
        mSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {

            }

            override fun onDone(utteranceId: String?) {

            }

            override fun onError(utteranceId: String?) {

            }

        })
        Task()
    }

    //开启抓拍任务
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Task() {
        camera = MyApplication.model!!.beginCamera()
        if (camera != null) {
            camera!!.setPreviewCallback(object : Camera.PreviewCallback {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPreviewFrame(arr: ByteArray?, camera: Camera?) {
                    if (arr != null) {
                        //对焦
                        camera!!.autoFocus(null)
                        //拍摄
                        //上传识别
                        //间隔 500 ms
                        if (TimeUtil.subtractionTime(indexDate) > 500) {
                            indexDate = Date()
                            data = arr
                            CloudRecognition()
                        }
                    }
                }
            })
        }
    }

    /**
     * 人流量检测
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun CloudRecognition() {
        Thread(Runnable {
            run {
                try {
                    var previewSize = camera!!.getParameters().getPreviewSize()
                    val areaW = previewSize.width - 100
                    val areaH = previewSize.height - 100
                    val req = JSONObject()
                    req.put(
                        "imagedata",
                        Base64Utils.ByteToBase64(Base64Utils.ByteToImage(data, previewSize.width, previewSize.height))
                    )
                    req.put("id", case_id)
                    req.put(
                        "area",
                        "100,100," + areaW + ",100," + areaW + "," + areaH + ",100," + areaH + ""
                    )
                    OkHttpUtil().sendPost(Apis.TrafflcIdentifyImageFaceInfo, req,
                        object : HttpCallBack {
                            override fun Error(call: Call?, e: IOException?) {

                            }

                            override fun Success(call: Call?, res: String?) {
                                println(res)
                                val rep = JSONObject(res)
                                if (rep.getInt("code") == 200 && rep.getJSONObject("data").getJSONObject("person_count").getInt("in") > 0){
                                    mSpeech!!.speak(
                                        "欢迎进屋，屋内灯光已开启。",
                                        TextToSpeech.QUEUE_ADD,
                                        null,
                                        "speech"
                                    )
                                } else if (rep.getInt("code") == 200 && rep.getJSONObject("data").getJSONObject("person_count").getInt("out") > 0){
                                    mSpeech!!.speak(
                                        "屋内没人，关闭灯光。",
                                        TextToSpeech.QUEUE_ADD,
                                        null,
                                        "speech"
                                    )
                                }
                            }
                        })
                } catch (v1: java.lang.Exception) {
                    v1.printStackTrace()
                }
            }
        }).start()
    }

}
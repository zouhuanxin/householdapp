package com.zhx.householdapp.app

import android.app.Application
import com.dhh.websocket.Config
import com.dhh.websocket.RxWebSocket
import com.zhx.householdapp.data.Model
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.*

class MyApplication:Application() {

    companion object{
        var model: Model = Model()
        //线程池对象
        //LinkedBlockingQueue 默认为无限大小 建议一定要传大小不然容易内存溢出
        //ArrayBlockingQueue 这个大小必传
        val ThreadPool = ThreadPoolExecutor(
            5, 100, 20, TimeUnit.SECONDS,
            ArrayBlockingQueue<Runnable>(100)
        )
        var pool: ExecutorService = Executors.newFixedThreadPool(50)
    }

    override fun onCreate() {
        super.onCreate()
        val config: Config = Config.Builder()
            .setShowLog(true) //show  log
            .setClient(OkHttpClient.Builder()
                .pingInterval(3, TimeUnit.SECONDS) // 设置心跳间隔，这个是3秒检测一次
                .build())
            .setShowLog(true, "your logTag")
            .setReconnectInterval(2, TimeUnit.SECONDS) //set reconnect interval
            .build()
        RxWebSocket.setConfig(config)
    }

}
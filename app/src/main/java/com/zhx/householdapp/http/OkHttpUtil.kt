package zhx.hello.usbweightv1.http

import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
/**
 * 存在一些复用问题会导致请求发不出去
 */
class OkHttpUtil {
    private val overtime = 60
    private val client = OkHttpClient.Builder()
        .connectTimeout(overtime.toLong(), TimeUnit.SECONDS) //设置连接超时时间
        .readTimeout(overtime.toLong(), TimeUnit.SECONDS) //设置读取超时时间
        .writeTimeout(overtime.toLong(), TimeUnit.SECONDS) //设置写入超时时间
        .build()

    companion object{
        private val okHttpUtil:OkHttpUtil = OkHttpUtil()

        fun getInstantce():OkHttpUtil{
            return okHttpUtil
        }
    }

    fun sendGet(url: String, h: HttpCallBack) {
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                h.Error(call, e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()
                response.body()!!.close()
                try {
                    h.Success(call, res)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun sendGet(url: String): JSONObject {
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val call = client.newCall(request)
        val res = call.execute().body()!!.string()
        return JSONObject(res)
    }

    fun sendPost(url: String, josn: JSONObject, h: HttpCallBack) {
        val requestBody = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            josn.toString()
        )
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                h.Error(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()
                println(res)
                try {
                    h.Success(call, res)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun sendPost(url: String, josn: JSONObject): JSONObject {
        val requestBody = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            josn.toString()
        )
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val call = client.newCall(request)
        val res = call.execute().body()!!.string()
        return JSONObject(res)
    }


}
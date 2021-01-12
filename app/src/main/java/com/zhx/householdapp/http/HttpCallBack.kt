package zhx.hello.usbweightv1.http

import okhttp3.Call
import org.json.JSONException
import java.io.IOException

interface HttpCallBack {

    fun Error(call: Call?, e: IOException?)
    //回调返回内容
    @Throws(IOException::class, JSONException::class)
    fun Success(call: Call?, res: String?)

}
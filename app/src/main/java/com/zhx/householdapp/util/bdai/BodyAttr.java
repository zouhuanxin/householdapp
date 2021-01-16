package com.zhx.householdapp.util.bdai;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;

/**
 * 人体检测和属性识别
 */
public class BodyAttr {
    private static String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_attr";

//    public static String body_attr() {
//        try {
//            // 本地文件路径
//            String filePath = "/Users/zhx/Downloads/IMG_2370.jpeg";
//            byte[] imgData = FileUtil.readFileByBytes(filePath);
//            String imgStr = Base64Util.encode(imgData);
//            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
//            String param = "image=" + imgParam;
//            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
//            String accessToken = AuthService.beginAuth();
//            String result = HttpUtil.post(url, accessToken, param);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    //url版
    public static String body_attr(String imgStr) {
        try {
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.beginAuth();
            String result = HttpUtil.post(url, accessToken, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //sdk版
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String body_byte(byte[] imgStr) {
        try {
            //AipBodyAnalysis client = new AipBodyAnalysis("23522165", "AO3D8iUZPaHaZxw8UjQNed8h", "ulqHm5nF1VLVAlIkResw8AiEEglFGtAX");
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("type", "cellphone,location");
           // byte[] file = Base64.getDecoder().decode(imgStr);
            JSONObject result = AuthService.beginAipBodyAnalysis().bodyAttr(imgStr, options);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 人流量统计-动态版
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String body_trafflc(String imagedata, String id, String area) throws JSONException, JSONException {
        AipBodyAnalysis client = new AipBodyAnalysis("23522165", "AO3D8iUZPaHaZxw8UjQNed8h", "ulqHm5nF1VLVAlIkResw8AiEEglFGtAX");
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("case_id", id);
        options.put("case_init", id);
        options.put("show", "false");
        options.put("area", area);

        String dynamic = "true";

        // 参数为二进制数组
        byte[] file = Base64.getDecoder().decode(imagedata);
        JSONObject res = client.bodyTracking(file, dynamic, options);
        return res.toString(2);
    }

    public static void main(String[] args) {
        //BodyAttr.body_attr();
        //body_trafflc();
    }
}
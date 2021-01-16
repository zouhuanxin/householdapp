package com.zhx.householdapp.util.bdai;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpTool {

    /**
     * 同步get请求
     * @param url
     * @return
     */
    public String Get(String url){
        String msg = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (response == null){
                return null;
            }
            msg = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

}

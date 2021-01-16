package com.zhx.householdapp.util.bdai;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.zhx.householdapp.util.TimeUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 获取token类
 */
public class AuthService {
    private static String identity = null;
    private static Map<String, Date> map = new HashMap<>();
    //记录当前返回的AipBodyAnalysis
    private static int index = 0;

    //AipBodyAnalysis集合
    private static List<AipBodyAnalysis> clients = new ArrayList<>();

    public synchronized static String beginAuth() {
        if (identity == null) {
            identity = getAuth();
            map.put(identity, new Date());
        } else if (CheckCache(map.get(identity))) {
            //身份失效
            identity = getAuth();
            map.put(identity, new Date());
        } else {
            //身份有效

        }
        return identity;
    }

    public synchronized static AipBodyAnalysis beginAipBodyAnalysis() {
        if (clients.size() == 0) {
            clients.add(new AipBodyAnalysis("23522165", "AO3D8iUZPaHaZxw8UjQNed8h", "ulqHm5nF1VLVAlIkResw8AiEEglFGtAX"));
            clients.add(new AipBodyAnalysis("23519086", "7cpw7gI1BtswWSSeqQo9jaA3", "x4YNiaNX3wF5Rs91Fih6Ah7yQtLo5SEK"));
            clients.add(new AipBodyAnalysis("23524316", "YR28Dzx58ud0SvRTqUmtf2jB", "0fKfvNW2KtTwUd6tWHDNpdn8Ml7g0nfw"));
            clients.add(new AipBodyAnalysis("23524318", "OQMr0HAbhHu06dHQYpc9QohI", "ztKv29D0gtjm8QipOuN1mFWEOstfW35o"));
            clients.add(new AipBodyAnalysis("23524438", "jr56bUI7g7CpDx3V2AfysXI4", "XsFvQQ6xgLwhi6gpV7w754zG8DvIvpU0"));
            clients.add(new AipBodyAnalysis("23524474", "bsPdswv9w73uIwX23sKNV1Ev", "899mkCq5GKzN5svxa5UB2NRQGR06Gk1F"));
            clients.add(new AipBodyAnalysis("23524482", "vjSdgtYcO2A8G1FlZudF9f7K", "wVg77eubxofflxe3oqhOeL64ACE8fYGN"));
            clients.add(new AipBodyAnalysis("23524488", "XAYsykPNErMo6r61Arh982aa", "GIdhYIH9pKG3DhnbGSLYTewhKT6l3WxW"));
            clients.add(new AipBodyAnalysis("23524493", "4hlYbHpsto8X2zv0GnG6N8wK", "vftZaiGzpc0vKETPHmtPKtMyb9t2C3O8"));
        }
        if (index == 9) {
            index = 0;
        }
        AipBodyAnalysis client = clients.get(index);
        index = index + 1;
        return client;
    }

    /**
     * 判断是否有缓存
     */
    private static boolean CheckCache(Date time) {
        if (time == null) {
            //没有缓存
            return false;
        } else {
            //判断缓存失效是否超过10分钟
            long dif = TimeUtil.subtractionTime(time);
            if (dif > 10) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获取权限token
     *
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
//        // 官网获取的 API Key 更新为你注册的
//        String clientId = "7cpw7gI1BtswWSSeqQo9jaA3";
//        // 官网获取的 Secret Key 更新为你注册的
//        String clientSecret = "x4YNiaNX3wF5Rs91Fih6Ah7yQtLo5SEK";
        // 官网获取的 API Key 更新为你注册的
        String clientId = "AO3D8iUZPaHaZxw8UjQNed8h";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "ulqHm5nF1VLVAlIkResw8AiEEglFGtAX";
        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.err.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            //System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

}
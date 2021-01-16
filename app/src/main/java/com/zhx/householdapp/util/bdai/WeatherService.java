package com.zhx.householdapp.util.bdai;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WeatherService {
    private OkHttpTool okHttpTool = new OkHttpTool();

    public JSONObject getTodayWeather() throws JSONException {
        JSONObject res = new JSONObject();
        //http://www.weather.com.cn/weather1dn/101240212.shtml
        //http://forecast.weather.com.cn/town/weather1dn/101240212034.shtml#input
        //http://forecast.weather.com.cn/town/weather1dn/101240212034.shtml#input
        String str = okHttpTool.Get("http://forecast.weather.com.cn/town/weather1dn/101240212034.shtml#input");
//        if (str.length() < 200) {
//            str = okHttpTool.Get("http://www.weather.com.cn/weather1dn/101240212.shtml");
//        }
        //开始解析
        Document document = Jsoup.parse(str);
        //当前天气
        String CurrentTemperature = document.getElementsByClass("todayLeft").get(0).getElementsByClass("tempDiv").get(0).getElementsByClass("temp").text();
        //当日天气最高温度
        String TopTemperature = document.getElementById("maxTempDiv").getElementsByTag("span").text().replace("℃", "");
        //当日天气最低温度
        String BottomTemperature = document.getElementById("minTempDiv").getElementsByTag("span").text().replace("℃", "");
        //当前预警
        //String yj1 = document.getElementsByClass("sk_alarm").get(0).getElementsByTag("a").get(0).text();
        String yj2 = document.getElementsByClass("todayLeft").get(0).getElementsByTag("p").get(0).getElementsByTag("span").get(0).text();
        String yj3 = document.getElementsByClass("todayLeft").get(0).getElementsByTag("p").get(1).getElementsByTag("span").get(0).text();
        res.put("CurrentTemperature", CurrentTemperature);
        res.put("TopTemperature", TopTemperature);
        res.put("BottomTemperature", BottomTemperature);
        //res.put("yj1",yj1);
        res.put("yj2", yj2);
        res.put("yj3", yj3);
        //感冒
        Elements lis = document.getElementsByClass("weather_shzs").get(0).getElementsByTag("ul").get(0).getElementsByTag("li");
        Elements lvs = document.getElementsByClass("lv").get(0).getElementsByTag("dl");
        for (int i = 0; i < lis.size(); i++) {
            if (lis.get(i).getElementsByTag("h2").text().equals("穿衣")){
                res.put("cy", "穿衣：" + lvs.get(i).getElementsByTag("dt").get(0).text() + lvs.get(i).getElementsByTag("dd").get(0).text());
            }
            if (lis.get(i).getElementsByTag("h2").text().equals("空气污染扩散")){
                res.put("kq", "空气：" + lvs.get(i).getElementsByTag("dt").get(0).text() + lvs.get(i).getElementsByTag("dd").get(0).text());
            }
        }
        return res;
    }

}

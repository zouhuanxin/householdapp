package zhx.hello.usbweightv1.http

class Apis {
    companion object {
        //val IP: String = "http://192.168.0.104:8083"
        val IP: String = "http://49.234.123.245:8082"
        var IdentifyImageFaceInfo: String = IP + "/IdentifyImageFaceInfo" //人体识别
        var TrafflcIdentifyImageFaceInfo: String = IP + "/TrafflcIdentifyImageFaceInfo"; //人流量识别-动态版
        var getTodayWeather = IP + "/getTodayWeather"
    }
}
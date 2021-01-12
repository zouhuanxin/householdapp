package zhx.hello.usbweightv1.http

class Apis {
    companion object {
        val IP:String = "http://192.168.0.101:8083"
        //     val IP:String = "http://172.20.10.6:8083"
        var IdentifyImageFaceInfo: String = IP+"/IdentifyImageFaceInfo" //人体识别
        var TrafflcIdentifyImageFaceInfo: String  = IP+"/TrafflcIdentifyImageFaceInfo"; //人流量识别-动态版
    }
}
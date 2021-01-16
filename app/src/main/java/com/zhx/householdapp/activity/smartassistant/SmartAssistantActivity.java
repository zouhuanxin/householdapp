package com.zhx.householdapp.activity.smartassistant;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.TextureView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhx.householdapp.R;
import com.zhx.householdapp.app.MyApplication;
import com.zhx.householdapp.service.SmartAssistantService;
import com.zhx.householdapp.util.NavigationManager;
import com.zhx.householdapp.util.TimeUtil;
import com.zhx.householdapp.util.bdai.WeatherService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import zhx.hello.usbweightv1.http.Apis;
import zhx.hello.usbweightv1.http.HttpCallBack;
import zhx.hello.usbweightv1.http.OkHttpUtil;

public class SmartAssistantActivity extends AppCompatActivity {

    private TextView time1;
    private TextView time2;
    private TextView weather;

    private CountDownTimer countDownTimer;
    private int index = 0;

    //需要播报的天气信息
    public static String weatherInfo = "当前未获取到天气信息";

    private Camera mCamera;
    private final ServiceConnection mConnection = (ServiceConnection) (new ServiceConnection() {
        public void onServiceConnected(@NotNull ComponentName name, @NotNull IBinder service) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(service, "service");
        }

        public void onServiceDisconnected(@NotNull ComponentName name) {
            Intrinsics.checkNotNullParameter(name, "name");
        }
    });
    private TextureView camreaview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationManager.setBottomNavigationColor(this);
        setContentView(R.layout.activity_smart_assistant);
        initView();
        initData();
        reqWeather();
    }

    private void initView() {
        time1 = (TextView) findViewById(R.id.time1);
        time2 = (TextView) findViewById(R.id.time2);
        weather = (TextView) findViewById(R.id.weather);
        camreaview = (TextureView) findViewById(R.id.camreaview);

        camreaview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    if (mCamera != null) {
                        mCamera.stopPreview();
                    }
                    mCamera = MyApplication.Companion.getModel().beginCamera();
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewTexture(surface);
                    mCamera.startPreview();
                    bindService(new Intent(SmartAssistantActivity.this, SmartAssistantService.class), mConnection, BIND_AUTO_CREATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void initData() {
        countDownTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTime();
                index++;
                if (index == 60) {
                    index = 0;
                    reqWeather();
                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void reqWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject rep = null;
                try {
                    rep = new JSONObject(getTodayWeather());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject finalRep = rep;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = finalRep.getJSONObject("data");
                            weatherInfo = "当前温度" + data.getString("CurrentTemperature") + "度,最高温" + data.getString("TopTemperature") + "，最低温" + data.getString("BottomTemperature")
                                    + data.getString("cy");
                            StringBuffer buff = new StringBuffer();
                            buff.append("当前温度" + data.getString("CurrentTemperature") + "度");
                            buff.append("\n" + data.getString("TopTemperature") + "/" + data.getString("BottomTemperature"));
                            weather.setText(buff);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private String getTodayWeather(){
        JSONObject rep = new JSONObject();
        try {
            rep.put("code",200);
            rep.put("data",new WeatherService().getTodayWeather());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rep.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private void updateTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time1.setText(TimeUtil.getCurrentTime().split(" ")[1]);
                time2.setText(TimeUtil.getCurrentTime().split(" ")[0]);
            }
        });
    }
}
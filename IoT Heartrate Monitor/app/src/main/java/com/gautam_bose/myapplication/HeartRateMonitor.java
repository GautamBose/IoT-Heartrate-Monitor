package com.gautam_bose.myapplication;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;
import io.particle.android.sdk.cloud.*;
import io.particle.android.sdk.utils.Async;
import java.io.IOException;
import android.os.AsyncTask;
//public int particleDevice = 36000;


public class HeartRateMonitor extends AppCompatActivity {
    //YOU MUST ADD YOUR DEVICEID, PARTICLE CLOUD USER AND PARTICLE CLOUD PASS HERE
    public final String deviceId = "#############";
    public final String cloudUser = "############";
    public final String cloudPass = "############";
    public ParticleDevice myDevice;
    public int bpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_heart_rate_monitor);
        ParticleCloudSDK.init(getApplicationContext());

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Integer>() {


            @Override
            public Integer callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(cloudUser, cloudPass);
                return 1;
            }

            @Override
            public void onSuccess(Integer integer) {
                    getDevice();
//                    Log.d("wef", "LoggedIN!");

            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                exception.printStackTrace();
            }
        });
    }
    private void getDevice() {
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, ParticleDevice>() {


            @Override
            public ParticleDevice callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                ParticleDevice d = ParticleCloudSDK.getCloud().getDevice(deviceId);

                return d;
            }

            @Override
            public void onSuccess(ParticleDevice device) {
                Log.d("device", "deviceFound!");
                myDevice = device;
                Log.d("wef", myDevice.getName());

                }

            @Override
            public void onFailure(ParticleCloudException exception) {
                exception.printStackTrace();
            }
        });

    }

    private void getbpm() {
        Log.d("bpm", "gettinbpm");
        if (myDevice != null) {
            Log.d("bpm", "actuallyGettinga");
            Async.executeAsync(myDevice, new Async.ApiWork<ParticleDevice, Integer>() {
                public Integer callApi(ParticleDevice particleDevice)
                        throws ParticleCloudException, IOException {
                    try {
                        bpm = myDevice.getIntVariable("bppm");
                        return 1;
                    } catch (ParticleDevice.VariableDoesNotExistException e) {
                        Log.d("caught", "cahgutya");
                        e.printStackTrace();
                        return 1;
                    }

                }

                @Override
                public void onSuccess(Integer value) {
                    Log.d("dwef", Integer.toString(bpm));
                }

                @Override
                public void onFailure(ParticleCloudException e) {
                    Log.e("some tag", "Something went wrong making an SDK call: ", e);
//                    Toaster.l(MyActivity.this, "Uh oh, something went wrong.");
                }

                @Override
                public void onTaskFinished() {
                    getbpm();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        final TextView beatsView =  (TextView)findViewById(R.id.wef);

        super.onStart();
        int count = 1;

        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
//                Log.d("task", "Doing task");
                getbpm();
                setText(beatsView, Integer.toString(bpm));

                handler.postDelayed(this, 3000);
            }
        };
        handler.post(task);




    }

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG,"Application resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG,"Application paused");
    }


}

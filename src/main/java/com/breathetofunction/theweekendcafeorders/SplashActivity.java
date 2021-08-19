package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.breathetofunction.theweekendcafeorders.Utils.makePostRequest;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String TAG = "Splash Activity";
        String SHARED_PREFS = Integer.toString(R.string.shared_prefs);
        Thread timer = new Thread(){
            public void run(){
                String id = null, password = null;
                try{
                    SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    id = prefs.getString("id","null");
                    password = prefs.getString("pass","null");
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(id.equals("null") || password.equals("null")){
                        Intent intent = new Intent("android.intent.action.Login");
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent("android.intent.action.MainActivity");
                        startActivity(intent);
                    }

                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

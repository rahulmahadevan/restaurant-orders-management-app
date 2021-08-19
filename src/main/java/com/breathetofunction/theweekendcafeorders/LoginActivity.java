package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

import static com.breathetofunction.theweekendcafeorders.Utils.makePostRequest;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LOGIN ACTIVITY";
    Button login;
    EditText tvId, tvPassword;
    String id, password;
    String SHARED_PREFS;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String requestUrl = getApplicationContext().getResources().getString(R.string.url)+"serverLogin";
        String authToken = getApplicationContext().getResources().getString(R.string.token) ;

        login = findViewById(R.id.btn_login);
        tvId = findViewById(R.id.tv_id);
        tvPassword = findViewById(R.id.tv_password);
        SHARED_PREFS = Integer.toString(R.string.shared_prefs);

        login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                id = tvId.getText().toString();
                password = tvPassword.getText().toString();
                String jsonBody = String.format("{ \"token\": \"%s\", \"id\": %s, \"pwd\": \"%s\" }", authToken, id, password);
                Log.d("JSON BODY", "onClick: " + jsonBody);
                //Auth with server

                new AsyncTask<String, String, String>() {
                    final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog.setMessage("Logging in...");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                    @Override
                    protected String doInBackground(String... params) {

                        try {
                            Log.d(TAG, "REQUEST URL: "+ requestUrl);
                            String response = makePostRequest(requestUrl, jsonBody, getApplicationContext());
                            Log.d(TAG, "HTTP RESPONSE: " + response);
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "";
                        }
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                        dialog.dismiss();
                        if(response.equals("")){
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String responseCode = jsonResponse.getString("responseCode");
                                JSONObject data = new JSONObject(jsonResponse.getString("data"));
                                String name = data.getString("name");
                                Log.d(TAG, "response: " + responseCode + ", name:" + name);
                                if (responseCode.equals("200")) {
                                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                                    editor.putString("id", id);
                                    editor.putString("pass", password);
                                    editor.apply();

                                    Intent intent = new Intent("android.intent.action.MainActivity");
                                    intent.putExtra("name", name);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.execute("");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
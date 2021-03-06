package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.breathetofunction.theweekendcafeorders.ui.main.SectionsPagerAdapter;
import com.breathetofunction.theweekendcafeorders.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static com.breathetofunction.theweekendcafeorders.Utils.makePostRequest;

public class MainActivity extends AppCompatActivity {

    String TAG = "MAIN ACTIVITY";
    private ActivityMainBinding binding;

    private int[] tabIcons = {
            R.drawable.tab_img,
            R.drawable.tab_img2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String requestUrl = getApplicationContext().getResources().getString(R.string.url)+"fetchMenu";
        String requestUrlForOrders = getApplicationContext().getResources().getString(R.string.url)+"fetchOrders";
        String authToken = getApplicationContext().getResources().getString(R.string.token);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        setUpTabIcons(tabs);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                String jsonBody = String.format("{ \"token\": \"%s\" }", authToken);
                Log.d(TAG, "onClick: " + jsonBody);
                //Auth with server and fetch Menu
                new AsyncTask<String, String, String>() {
                    final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog.setMessage("Loading Menu...");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            String response = makePostRequest(requestUrl, jsonBody, getApplicationContext());
                            Log.d(TAG, "HTTP Response: " + response);
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
                                JSONArray data = new JSONArray(jsonResponse.getString("data"));
                                Log.d(TAG, "JSON DATA ARRAY: "+data);
                                if (responseCode.equals("200")) {
                                    Intent intent = new Intent("android.intent.action.MenuSelection");
                                    intent.putExtra("menuData", data.toString());
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

    private void setUpTabIcons(TabLayout tabs){
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
    }
}


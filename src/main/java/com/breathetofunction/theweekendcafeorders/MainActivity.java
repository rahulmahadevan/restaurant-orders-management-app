package com.breathetofunction.theweekendcafeorders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.breathetofunction.theweekendcafeorders.ui.main.SectionsPagerAdapter;
import com.breathetofunction.theweekendcafeorders.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    String url = "http://192.168.29.233:8020/LATEST/documents?uri=/cafe/data/menu.xml";
    String username = "turingmachine";
    String password = "admin";
    private int[] tabIcons = {
            R.drawable.tab_img,
            R.drawable.tab_img2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            @Override
            public void onClick(View view) {
                new HttpGetMenu().execute(url, username, password);
            }
        });
    }

    private void setUpTabIcons(TabLayout tabs){
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
    }

    class HttpGetMenu extends AsyncTask<String, Void, String> {

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching Menu...");
            dialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("HTTP TESTING", "doInBackground: "+strings[0]);

                String auth = strings[1] + ":" + strings[2];
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
                String authHeaderValue = "Basic:" + new String(encodedAuth);
                connection.setRequestProperty("Authorization",authHeaderValue);
                Log.d("AUTH HEADER", "doInBackground: "+ authHeaderValue);

                Log.d("HTTP RESPONSE CODE", "doInBackground: "+connection.getResponseCode());

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while((line = br.readLine()) != null){
                        response.append(line+"\n");
                    }
                    return response.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            dialog.dismiss();
            Log.d("TESTING", "onPostExecute: "+response);
            if(response == null){
                Toast.makeText(getApplicationContext(),"Oops..Looks like Server is down", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent("android.intent.action.OrderInfo");
                intent.putExtra("response",response);
                startActivity(intent);
            }
        }
    }
}


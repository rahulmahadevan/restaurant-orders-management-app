package com.breathetofunction.theweekendcafeorders;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static String makePostRequest(String stringUrl, String payload, Context context) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();
        uc.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }

    public static String capitalizeFirst(String line){
        String[] words = line.split(" ");
        StringBuilder result = new StringBuilder();
        for(int i=0;i< words.length;i++){
            words[i] = words[i].toLowerCase();
            words[i] = (words[i].charAt(0) + "").toUpperCase() + words[i].substring(1);
            result.append(words[i]);
            if(i < words.length - 1){
                result.append(" ");
            }
        }
        return result.toString();
    }
}

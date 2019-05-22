package com.example.capstone;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Point extends AsyncTask<String, Integer, Point> {
    // 위도
    public double x;
    // 경도
    public double y;
    public String addr;
    // 포인트를 받았는지 여부
    public boolean havePoint;

    Point(String addr){
        this.addr = addr;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("x : ");
        builder.append(x);
        builder.append(" y : ");
        builder.append(y);
        builder.append(" addr : ");
        builder.append(addr);

        return builder.toString();
    }

    @Override
    protected Point doInBackground(String... strings) {
        String json = null;
        String clientId = "6h70bzzqaf";// 애플리케이션 클라이언트 아이디값";
        String clientSecret = "3PlxCcT6c4kY1Yi9mlAqN0UKDu2K4lVZFfMpAKoH";// 애플리케이션 클라이언트 시크릿값";
        try {
            addr = URLEncoder.encode(addr, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?coordinate=" + addr; // json
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            Log.d("conProperty", String.valueOf(con.getRequestProperties()));
            Log.d("conExpress", String.valueOf(con));
            Log.d("responseError", String.valueOf(con.getResponseCode()));
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            json = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json == null) {
            return this;
        }

        Log.d("TEST2", "json => " + json);

        Gson gson = new Gson();
        NaverData data = new NaverData();
        try {
            data = gson.fromJson(json, NaverData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.result != null) {
            x = data.result.items.get(0).point.x;
            y = data.result.items.get(0).point.y;
            havePoint = true;
        }

        return null;
    }
}
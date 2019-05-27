package com.example.capstone;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Point extends AsyncTask<String, Integer, NaverPlaceData> {
    Spinner spinner;
    // 위도
    public double x;
    // 경도
    public double y;
    public String addr;
    // 포인트를 받았는지 여부
    public boolean havePoint;
    // 테마
    private String query;

    Point(String addr, Spinner spinner){
        this.addr = addr;
        this.spinner = spinner;
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
    protected NaverPlaceData doInBackground(String... strings) {
        String json = null;
        String clientId = "6h70bzzqaf";// 애플리케이션 클라이언트 아이디값";
        String clientSecret = "3PlxCcT6c4kY1Yi9mlAqN0UKDu2K4lVZFfMpAKoH";// 애플리케이션 클라이언트 시크릿값";
        try {
//            String query = "음식";
//            Spinner spinner = (Spinner)findViewById(R.id.spinner);
            query = (String) spinner.getSelectedItem();

            query = URLEncoder.encode(query, "UTF-8");
            addr = URLEncoder.encode(addr, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + query + "&coordinate=" + addr; // json
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

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
            return null;
        }

        Log.d("TEST2", "json => " + json);


        Gson gson = new Gson();
        NaverPlaceData data = new NaverPlaceData();
        data.query = query;
        try {
            data = gson.fromJson(json, NaverPlaceData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("jsonTest", data.toString());

       return data;
    }
}
package com.example.capstone;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class Point extends AsyncTask<String, Integer, Pair<NaverPlaceData, NaverPlaceData>> {
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
    // json
    private String json = null;

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
    protected Pair<NaverPlaceData, NaverPlaceData>  doInBackground(String... strings) {
        NaverPlaceData metroPlaceData = new NaverPlaceData();
        NaverPlaceData naverPlaceData = new NaverPlaceData();

        String clientId = "6h70bzzqaf";// 애플리케이션 클라이언트 아이디값";
        String clientSecret = "3PlxCcT6c4kY1Yi9mlAqN0UKDu2K4lVZFfMpAKoH";// 애플리케이션 클라이언트 시크릿값";

        try {
            query = (String) spinner.getSelectedItem();
            String metroQuery = "전철역";

            metroQuery = URLEncoder.encode(metroQuery, "UTF-8");
            addr = URLEncoder.encode(addr, "UTF-8");

            String metroURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + metroQuery + "&coordinate=" + addr; // json
//            Log.d("api Test", "URL => " + apiURL);
            connectApi(metroURL, clientId, clientSecret);

            Log.d("Metro Test", "json => " + json);

            metroPlaceData = dataFromJson();

            String metroPos = metroPlaceData.places.get(0).x + "," + metroPlaceData.places.get(0).y;
            Log.d("extract metro", String.valueOf(metroPos));

            json = null;

            query = URLEncoder.encode(query, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + query + "&coordinate=" + metroPos; // json
            connectApi(apiURL, clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        naverPlaceData = dataFromJson();

       return new Pair<NaverPlaceData, NaverPlaceData>(naverPlaceData, metroPlaceData);
    }

    private void connectApi(String urlStr, String clientId, String clientSecret) throws IOException {
        URL url = new URL(urlStr);
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
    }

    private NaverPlaceData dataFromJson(){
        Log.d("TEST2", "json => " + json);

        Gson gson = new Gson();
        NaverPlaceData data = new NaverPlaceData();
        data.query = query;
        try {
            data = gson.fromJson(json, NaverPlaceData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

       // Log.d("jsonTest", data.toString());

        return data;
    }
}
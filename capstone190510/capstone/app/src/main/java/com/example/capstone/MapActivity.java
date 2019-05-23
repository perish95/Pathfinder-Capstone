/*
 * Copyright 2018-2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.capstone;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.naver.maps.geometry.Coord;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationSource locationSource;

    private double[] coord_Array = {0,0}; // latitude, longitude
    // 람다식 내에서 변수를 가져오기 위해 배열을 썼지만 Side Effect 이슈 존재하는 코딩이라고 함
    // 근데 다른 방법은 더 모르겠어서 그냥 씀

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//        } // 상단 뒤로가기 버튼 있는 막대

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(new NaverMapOptions().locationButtonEnabled(true));
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"위치 권한 승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"위치 권한을 아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationSource = null;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        naverMap.setLocationSource(locationSource);
        LocationTrackingMode mode = naverMap.getLocationTrackingMode(); // LocationTrackingMode는 None / Follow / NoFollow / Face 모드 있음
        locationSource.setCompassEnabled(mode == LocationTrackingMode.Follow || mode == LocationTrackingMode.Face);

        naverMap.addOnLocationChangeListener((coord) -> {
            coord_Array[0] = coord.getLatitude(); // 위도 경도 더블형으로 받은거임 알아서 갖다 쓰셈
            coord_Array[1] = coord.getLongitude();

            // 디버그용
            Toast.makeText(this, getString(R.string.check_coord, coord_Array[0], coord_Array[1]), Toast.LENGTH_SHORT).show(); // 맵에 위치 변경 리스너 추가 후 현재 사용자의 위치가 변경되면 좌표 자동 출력

            // 요청 버튼
            final Button requestButton = (Button) findViewById(R.id.requestButton);

            // 요청 버튼에 리스너 달아주고
            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String addr = String.valueOf(coord_Array[1]) + "," + String.valueOf(coord_Array[0]);
                    Log.d("testCoord", addr);
                    new Point(addr).execute();
                /*
                curl "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query={장소_명칭}&coordinate={검색_중심_좌표}" \
                -H "X-NCP-APIGW-API-KEY-ID: {애플리케이션 등록 시 발급받은 client id값}" \
                -H "X-NCP-APIGW-API-KEY: {애플리케이션 등록 시 발급받은 client secret값}" -v
                */
                }
            });
        });


    }

    /*
    private void receiveObject(JSONObject data){
        recyclerView.setVisibility(View.GONE);
        objectResultLo.setVisibility(View.VISIBLE);
        try{
            mReceiveTv.setText(data.toString());
            mReceiveNationTv.setText("nation : "+data.getString("nation"));
            mReceiveNameTv.setText("name : "+data.getString("nation"));
            mReceiveAddressTv.setText("address : "+data.getString("address"));
            mReceiveAgeTv.setText("age : "+data.getString("age"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
}

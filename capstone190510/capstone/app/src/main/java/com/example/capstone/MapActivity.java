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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationSource locationSource;

    private User user;
    private String friendKey;
    private double partnerLati; //상대방의 좌표
    private double partnerLongi; //상대방의 좌표
    private boolean knowYourPos;
    private boolean knowMyPos;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");

    private double[] coord_Array = {0, 0}; // latitude, longitude
    // 람다식 내에서 변수를 가져오기 위해 배열을 썼지만 Side Effect 이슈 존재하는 코딩이라고 함
    // 근데 다른 방법은 더 모르겠어서 그냥 씀

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        Intent recv = getIntent();

        if ((User) recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            Log.d("CHECK", "[MapActivity]catch : " + user.get_nickname());
        }

        if(recv.getStringExtra("FriendID") != null){
            friendKey = recv.getStringExtra("FriendID");
            Log.d("CHECK", "[MapActivity]catch, friendkey : " + friendKey);
        }

        updatePosition(); // partnerLongi, partnerLati에 좌표를 넣어주는 메소드

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//        } // 상단 뒤로가기 버튼 있는 막대

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "위치 권한 승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "위치 권한을 아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
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
            user.latitude = coord.getLatitude();
            user.longitude = coord.getLongitude();
            databaseReference.child(user.get_nickname()).child("latitude").setValue(user.latitude);
            databaseReference.child(user.get_nickname()).child("longitude").setValue(user.longitude);
            knowMyPos = true;
            calcPosition();

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

                    try {
                        NaverPlaceData placeData = new Point(addr).execute().get();
                        // 네트워크 관련 처리는 메인 스레드에서 수행 금지
                        // 따라서 비동기 태스크인 AsyncTask에서 백그라운드로 작업 수행
                        // execute로 실행하고 get으로 doInBackground의 return 값 받아옴

                        Marker marker = new Marker();
                        marker.setPosition(new LatLng(placeData.places.get(1).y, placeData.places.get(1).x ));
                        marker.setMap(naverMap);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    void updatePosition(){ //상대의 위치정보를 얻어오기 위한 메소드
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(User.class).get_nickname().equals(friendKey)){
                    partnerLati = dataSnapshot.getValue(User.class).latitude;
                    partnerLongi = dataSnapshot.getValue(User.class).longitude;
                    Log.d("check","enter for " + partnerLati);
                    knowYourPos = true;
                    calcPosition();
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    void calcPosition(){
        if(knowMyPos && knowYourPos) {
            Log.d("Fuck", "checkpos " + user.latitude + " " + user.longitude + " " + partnerLati + " " + partnerLongi);
            center_of_two_point(user.latitude, user.longitude, partnerLati, partnerLongi);
            Log.d("Fuck"," checkpos " + center_of_two_point(user.latitude, user.longitude, partnerLati, partnerLongi).left + " " + center_of_two_point(user.latitude, user.longitude, partnerLati, partnerLongi).left);
        }
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {//두 경위도 좌표 사이 거리
        double eps = 1e-9;//실수 오차 잡아줄 엡실론(epsilon)
        if ((lat1 - eps < lat2) && (lat1 < lat2 + eps) && (lon1 - eps < lon2) && (lon1 < lon2 + eps)) {//경도 1과 경도 2, 위도1과 위도2의 차이가 eps보다 작다면 같다고 판단
            return 0;
        }
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        Log.d("test distance",String.valueOf(dist));
        return (dist);
    }
    private Pair<Double, Double> center_of_two_point(double lat1, double lon1, double lat2, double lon2) {//두 경위도 좌표의 중간 지점의 경위도 좌표
        double theta = Math.toRadians(lon2 - lon1);
        lat1=Math.toRadians(lat1);
        lat2=Math.toRadians(lat2);
        lon1=Math.toRadians(lon1);
        double x = Math.cos(lat1) * Math.cos(theta);
        double y = Math.cos(lat2) * Math.sin(theta);
        double resLat = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + x) * (Math.cos(lat1) + x) + y * y));
        double resLon = lon1 + Math.atan2(y, Math.cos(lat1) + x);
        resLat = Math.toDegrees(resLat);
        resLon = Math.toDegrees(resLon);
        Log.d("test Latitude",String.valueOf(resLat));
        Log.d("test Longitute",String.valueOf(resLon));
        knowYourPos = false;
        knowMyPos = false;
        return new Pair<Double,Double>(resLat, resLon);//중간지점 경위도 좌표 반환
    }
}
class Pair<L, R> {
    final L left;
    final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }
}
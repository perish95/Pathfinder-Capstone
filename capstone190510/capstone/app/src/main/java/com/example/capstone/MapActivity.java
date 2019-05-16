package com.example.capstone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;
import com.naver.maps.map.widget.ZoomControlView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // 위치 권한 요청 코드
    private FusedLocationSource locationSource; // 위치 반환 구현체 (google play service)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //맵 생성 및 UI 표시
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        else{
            mapFragment = MapFragment.newInstance(new NaverMapOptions().locationButtonEnabled(true));
//            Log.v("hi", "hi checking : " + mapFragment.toString());
        }
        mapFragment.getMapAsync(naverMap -> {
            LocationButtonView locationButtonView = findViewById(R.id.location); // 위치 검색 UI
            locationButtonView.setMap(naverMap);
            locationButtonView.setOnClickListener(v -> naverMap.setLocationTrackingMode(LocationTrackingMode.Follow)); // 재검색 버튼에 리스너 추가
            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setVisible(true);
        });

        //
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        checkAccessFinePermission();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource); // 구현체 맵에 set
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow); // 맵에 위치 추적 추가

        naverMap.addOnOptionChangeListener(() -> {
            LocationTrackingMode mode = naverMap.getLocationTrackingMode();

            locationSource.setCompassEnabled(mode == LocationTrackingMode.Follow || mode == LocationTrackingMode.Face);
        });
//        naverMap.addOnLocationChangeListener(location ->
//                Toast.makeText(this, location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show()); // 맵에 위치 변경 리스너 추가

//        LocationTrackingMode trackingMode = naverMap.getLocationTrackingMode();

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

    private void checkAccessFinePermission(){
        int accessflPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(accessflPermissionCheck != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "위치 권한 승인이 필요합니다", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"현재 위치 확인을 위해 위치 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
                Toast.makeText(this,"현재 위치 확인을 위해 위치 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }
        Log.v("checkPermission", "Check Permission : " + ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) +
                "Location Permission : " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)); // 권한 체크 디버깅 로그
    }

//    public boolean pressedLocationButtion

}
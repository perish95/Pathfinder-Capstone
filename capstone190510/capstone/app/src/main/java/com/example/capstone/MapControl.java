package com.example.capstone;

import android.support.annotation.NonNull;
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

public class MapControl {
    private MapActivity mapActivity;
    private String addr;
    private NaverMap naverMap;
    private Pair<NaverPlaceData, NaverPlaceData> placeAndMetro;
    private NaverPlaceData placeData;
    private NaverPlaceData metroPlaceData;
    private ArrayList<Marker> markerList = new ArrayList<Marker>();
    private ArrayList<InfoWindow> infoList = new ArrayList<InfoWindow>();
    private LinkedHashMap<Marker, MarkerInfo> markerInfoList = new LinkedHashMap<Marker, MarkerInfo>();
    private Spinner spinner;
    private String prevQuery;
    private boolean possibleGetPlace;
    private NaverPlaceData.places currentReturnPlace;

    MapControl(MapActivity mapActivity, String addr, NaverMap naverMap, Spinner spinner) {
        this.mapActivity = mapActivity;
        this.addr = addr;
        this.naverMap = naverMap;
        this.spinner = spinner;

        try {
            placeAndMetro = new Point(addr, spinner).execute().get();
            placeData = placeAndMetro.left;
            metroPlaceData = placeAndMetro.right;
            Toast.makeText(mapActivity,  placeData.distanceCheck(), Toast.LENGTH_LONG).show();
            // 네트워크 관련 처리는 메인 스레드에서 수행 금지
            // 따라서 비동기 태스크인 AsyncTask에서 백그라운드로 작업 수행
            // execute로 실행하고 get으로 doInBackground의 return 값 받아옴

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        drawMarker();
    }

    private void drawMarker() {
        for (NaverPlaceData.places place : placeData.places) { // 사용자 위치 근방 마커 5개 띄우기
            Marker marker = new Marker();
            marker.setPosition(new LatLng(place.y, place.x));
            marker.setMap(naverMap);

            markerList.add(marker);

            markerInfoList.put(marker, new MarkerInfo());

            if (markerInfoList.get(marker) != null) {
                MarkerInfo markerInfo = markerInfoList.get(marker);
                markerInfo = markerInfoList.get(marker);
                if (markerInfo == null) break;

                marker.setTag(place.name); // 마커의 태그를 장소 이름으로 지정

                assert markerInfo != null;
                markerInfo.setMapActivity(mapActivity);
                markerInfo.setMarker(marker);
                markerInfo.setName(place.name);
                markerInfo.setContent();

                String info = new String();
                info += place.name + '\n' + place.road_address + '\n' + place.phone_number;

                markerInfo.setDetailed_content(info);
                markerInfo.setOpenState(State.OPEN);

                drawInfoWindow(marker); // 맵 띄우자마자 인포창 그리기
            }

            // 마커에 추가할 리스너
            Overlay.OnClickListener listener = overlay -> {
                // 리스너가 동작하면 정보창 열고 닫기

                MarkerInfo markerInfo = markerInfoList.get(marker);
                if (markerInfo.marker != null) {
                    switch (markerInfo.openState) { // 마커에 이름 -> 상세 정보 -> 정보창 닫기 순 기능 구현중
                        case OPEN:
                            markerInfo.setContent();
                            markerInfo.seeDetail();
                            markerInfo.markerAdapterChange();
                            markerInfo.setOpenState(State.DETAIL);
                            break;
                        case DETAIL:
                            markerInfo.infoWindow.close();
                            markerInfo.seeBasic();
                            markerInfo.markerAdapterChange();
                            markerInfo.setOpenState(State.CLOSE);
                            possibleGetPlace = true;
                            currentReturnPlace = place;
                            break;
                        case CLOSE:
                            markerInfo.infoWindow.open(marker);
                            markerInfo.setOpenState(State.OPEN);
                            possibleGetPlace = false;
                            break;
                    }
                }

                return true;
            };

            marker.setOnClickListener(listener); // 마커에 터치 리스너 달아주기
        }
    }

    private void drawInfoWindow(Marker marker) {
        InfoWindow infoWindow = new InfoWindow(); // 정보창
        infoList.add(infoWindow);
        MarkerInfo markerInfo = markerInfoList.get(marker);

        markerInfo.setInfoWindow(infoWindow);

        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapActivity) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {

                return (CharSequence) markerInfo.content;
            }
        });
        infoWindow.open(marker);

        markerInfo.setInfoWindow(infoWindow);

        drawMiddle(naverMap, new Pair<Double, Double>(metroPlaceData.places.get(0).x, metroPlaceData.places.get(0).y)); // 중간 지점 표시

    }


    private void drawMiddle(NaverMap naverMap, Pair<Double, Double> centerPos){
        Marker marker = new Marker();
        marker.setPosition(new LatLng(centerPos.right, centerPos.left));
        marker.setMap(naverMap);

        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapActivity) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {

                return (CharSequence) "만남 추천 역";
            }
        });
        infoWindow.open(marker);

        LatLng latLng = new LatLng(centerPos.right, centerPos.left);
        CameraPosition cameraPosition = new CameraPosition(latLng, 14);
        naverMap.setCameraPosition(cameraPosition);
    }

    public String getPrevQuery(){
        return prevQuery;
    }

    public void removeMarker(){
        for(InfoWindow infoWindow : infoList){
            infoWindow.setMap(null);
        }
        infoList.clear();


        for(Marker marker : markerList){
            marker.setMap(null);
        }
        markerList.clear();
    }

    public NaverPlaceData.places getPlaceData() {
        if(possibleGetPlace) return currentReturnPlace;
        else return null;
    }
}

class MarkerInfo {
    protected Marker marker;
    protected InfoWindow infoWindow;
    protected State openState;
    protected String name;
    protected String content;
    protected String detailed_content;
    protected MapActivity mapActivity;

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public void setInfoWindow(InfoWindow infoWindow) {
        this.infoWindow = infoWindow;
    }
    public void setOpenState(State state) {
        openState = state;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setContent() {
        content = name;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDetailed_content(String detailed_content) {
        this.detailed_content = detailed_content;
    }
    public void seeDetail() {
        content = detailed_content;
    }
    public void seeBasic() {
        content = name;
    }
    public void setMapActivity(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    public void markerAdapterChange(){
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(mapActivity) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {

                return (CharSequence) content;
            }
        });
    }
}

enum State {
    OPEN, DETAIL, CLOSE;
}
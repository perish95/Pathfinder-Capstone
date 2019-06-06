package com.example.capstone;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NaverPlaceData {
    public String status;
    public meta meta;
    public List<places> places;
    public String errorMessage;
    public String errorCode;

    //
    public String query;

    public class meta{
        public int totalCount;
        public int count;
    }

    public class places {
        public String name;
        public String road_address;
        public String jibun_address;
        public String phone_number;
        public double x; // 경도 longitude
        public double y; // 위도 latitude
        public double distance;
        public String sessionId;
    }

    // 추천 장소 거리 인근 1km 외에는 삭제
    public String distanceCheck(){
        if(places == null) return null;

        for(Iterator<places> iter = places.iterator(); iter.hasNext();){
            places place = iter.next();
            if(place.distance > 1000) iter.remove();
        }

        if(places.isEmpty()){
            return "인근 1km 내 해당 테마로 추천할 장소가 없습니다.";
        }

        return "추천 장소 검색이 완료됐습니다.";
    }

    @Override
    public String toString() {
        if (places == null) {
            return String.format("errorMessage : %s, errorCode : %s", errorMessage, errorCode);
        } else {
            Log.d("places x", String.valueOf(places.get(2).x));
            return places.get(0).toString();
        }
    }
}
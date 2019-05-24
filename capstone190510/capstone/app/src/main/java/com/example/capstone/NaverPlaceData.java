package com.example.capstone;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NaverPlaceData {
    public String status;
    public meta meta;
    public List<places> places;
    public String errorMessage;
    public String errorCode;
//
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("status: ");
//        builder.append(status);
//        builder.append("\n");
//
//        return super.toString();
//    }

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
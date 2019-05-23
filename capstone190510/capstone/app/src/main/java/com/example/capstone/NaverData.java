package com.example.capstone;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NaverData {
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
        public double distance;

        public point point;

        public class point {
            public double x;
            public double y;
        }

        public String sessionId;
    }


    @Override
    public String toString() {
        if (places == null) {
            return String.format("errorMessage : %s, errorCode : %s", errorMessage, errorCode);
        } else {
            Log.d("places name", places.get(2).name);
            return places.get(0).toString();
        }
    }

}
package com.example.capstone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable {
    public int _number;
    public String _name;
    //public List<String> _friendList; //새로 추가됬음 190507
    public HashMap<String, String> friendsMap;
    public ArrayList<String> friendRequest;
    public double latitude; //위도
    public double longitude; //경도
    public boolean waitAccept;

    private String _id;
    private String _password;
    private String _nickname;

    //private static final long serialVersionUID = 1L;

    User(String number, String id, String password, String name, String nickname) {
        _number = Integer.parseInt(number);
        _id = id;
        _password = password;
        _name = name;
        _nickname = nickname;
        friendsMap = new HashMap<>();
        friendsMap.put(" "," ");
        friendRequest = new ArrayList<String>();
        friendRequest.add(" ");
        latitude = 0;
        longitude = 0;
        waitAccept = false;
    }

    User(){
        //Default Constructor
    }

    public String get_id(){
        return _id;
    }
    public String get_password(){
        return _password;
    }
    public String get_nickname(){ return _nickname; }

    //public List<String> get_friendList(){ return _friendList; }
}
package com.example.capstone;

import java.io.Serializable;
import java.util.ArrayList;

public class Member implements Serializable {
    public int _number;
    private String _id;
    private String _password;
    public String _name;
    private ArrayList<String> friendList = new ArrayList<>(); //새로 추가됬음 190507

    Member(String number, String id, String password, String name){
        _number = Integer.parseInt(number);
        _id = id;
        _password = password;
        _name = name;
    }

    public String getId(){
        return _id;
    }
    public String get_password(){
        return _password;
    }
}

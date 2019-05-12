package com.example.capstone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public int _number;
    public String _name;

    private String _id;
    private String _password;
    private String _nickname;
    private List<String> _friendList; //새로 추가됬음 190507

    private static final long serialVersionUID = 1L;

    User(String number, String id, String password, String name, String nickname) {
        _number = Integer.parseInt(number);
        _id = id;
        _password = password;
        _name = name;
        _nickname = nickname;
        _friendList = new ArrayList<>();
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
    public List<String> get_friendList(){ return _friendList; }
    public String get_nickname(){ return _nickname; }
}

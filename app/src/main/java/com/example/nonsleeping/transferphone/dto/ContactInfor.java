package com.example.nonsleeping.transferphone.dto;

public class ContactInfor {
    String _ID;
    String id = "";
    String name = "";
    String phoneNumber = "";
    int type = -99;

    public ContactInfor(String id, String name, String phoneNumber, int type, String _ID) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this._ID = _ID;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

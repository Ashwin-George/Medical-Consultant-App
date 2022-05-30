package com.example.android.callingapp.DataObjects;

public class LoginCredential {
    private String phoneNo;
    private String password;
    private String name;

    public LoginCredential(){}

    public LoginCredential(String phoneNo, String password, String name) {
        this.phoneNo = phoneNo;
        this.password = password;
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}

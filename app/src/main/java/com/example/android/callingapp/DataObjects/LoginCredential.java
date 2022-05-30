package com.example.android.callingapp.DataObjects;

public class LoginCredential {
    private String phoneNo;
    private String password;

    public LoginCredential(){}

    public LoginCredential(String phoneNo, String password) {
        this.phoneNo = phoneNo;
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }
}

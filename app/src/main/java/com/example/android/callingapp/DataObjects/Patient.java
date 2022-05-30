package com.example.android.callingapp.DataObjects;

public class Patient {
    private String name;
    private String phone;
    private String sex;
    private String status;
    private String description;
    private Doctor doctor;

    public Patient(){}

    public Patient(String name, String phone, String sex, String status, String description, Doctor doctor) {
        this.name = name;
        this.phone = phone;
        this.sex = sex;
        this.status = status;
        this.description = description;
        this.doctor = doctor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}

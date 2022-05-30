package com.example.android.callingapp.DataObjects;

import androidx.annotation.Nullable;

public class Doctor {
    private String name;
    private String specialty;
    private String telephone;
    public Doctor(){};

    public Doctor(String name, String speciality, String telephone){
        this.name = name;
        this.specialty = speciality;
        this.telephone = telephone;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Doctor doc=(Doctor) obj;
        if(this.name==doc.name && this.specialty==doc.specialty && this.telephone==doc.telephone)
            return true;
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

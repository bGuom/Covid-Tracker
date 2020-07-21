package com.mobicom.covidtracker.Models;

public class UserKey {

    public String diagnosisKey;
    public String contactNumber;
    public String dateJoined;

    public UserKey(){}

    public UserKey(String diagnosisKey, String contactNumber, String dateJoined) {
        this.diagnosisKey = diagnosisKey;
        this.contactNumber = contactNumber;
        this.dateJoined = dateJoined;
    }

    public String getDiagnosisKey() {
        return diagnosisKey;
    }

    public void setDiagnosisKey(String diagnosisKey) {
        this.diagnosisKey = diagnosisKey;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }
}

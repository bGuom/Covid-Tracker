package com.mobicom.covidtracker.Models;

import java.util.Date;

public class ContactData {

    private String date;
    private String time;
    private int level;
    private String key;

    public ContactData(String date, String time, int level, String key) {
        this.date = date;
        this.time = time;
        this.level = level;
        this.key = key;
    }

    public ContactData() {
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getLevel() {
        return level;
    }

    public String getKey() {
        return key;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
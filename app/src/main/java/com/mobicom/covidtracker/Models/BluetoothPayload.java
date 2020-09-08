package com.mobicom.covidtracker.Models;

import com.google.gson.Gson;
import com.mobicom.covidtracker.Utils.KeyHandler;

public class BluetoothPayload {

    private String broadcastID;
    private int key;

    private String btVersion;
    private String batteryPercentage;
    private String cpuTemp;
    private String batteryCapacity;

    public BluetoothPayload(){}

    public BluetoothPayload(String broadcastID, int key, String btVersion, String batteryPercentage, String cpuTemp, String batteryCapacity) {
        this.broadcastID = broadcastID;
        this.key =key;
        this.btVersion = btVersion;
        this.batteryPercentage = batteryPercentage;
        this.cpuTemp = cpuTemp;
        this.batteryCapacity = batteryCapacity;
    }

    public String getBroadcastID() {
        return broadcastID;
    }

    public void setBroadcastID(String broadcastID) {
        this.broadcastID = broadcastID;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getBtVersion() {
        return btVersion;
    }

    public void setBtVersion(String btVersion) {
        this.btVersion = btVersion;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getCpuTemp() {
        return cpuTemp;
    }

    public void setCpuTemp(String cpuTemp) {
        this.cpuTemp = cpuTemp;
    }

    public String getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(String batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public String getCommaSeparated(){
        return broadcastID+","+key+","+ btVersion +","+batteryPercentage+","+cpuTemp+","+batteryCapacity;
    }


}

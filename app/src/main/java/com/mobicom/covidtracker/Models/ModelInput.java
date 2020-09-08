package com.mobicom.covidtracker.Models;

public class ModelInput {

    private Float senderBatteryCap;
    private Float senderBatteryLevel;
    private Float receiverBatteryLevel;
    private Float senderBTVersion;
    private Float senderTemp;
    private Float receiverTemp;
    private Float receiverBTVersion;
    private Float rssi;

    public ModelInput(Float senderBatteryCap, Float senderBatteryLevel, Float receiverBatteryLevel, Float senderBTVersion, Float senderTemp, Float receiverTemp, Float receiverBTVersion, Float rssi) {
        this.senderBatteryCap = senderBatteryCap;
        this.senderBatteryLevel = senderBatteryLevel;
        this.receiverBatteryLevel = receiverBatteryLevel;
        this.senderBTVersion = senderBTVersion;
        this.senderTemp = senderTemp;
        this.receiverTemp = receiverTemp;
        this.receiverBTVersion = receiverBTVersion;
        this.rssi = rssi;
    }

    public Float getSenderBatteryCap() {
        return senderBatteryCap;
    }

    public void setSenderBatteryCap(Float senderBatteryCap) {
        this.senderBatteryCap = senderBatteryCap;
    }

    public Float getSenderBatteryLevel() {
        return senderBatteryLevel;
    }

    public void setSenderBatteryLevel(Float senderBatteryLevel) {
        this.senderBatteryLevel = senderBatteryLevel;
    }

    public Float getReceiverBatteryLevel() {
        return receiverBatteryLevel;
    }

    public void setReceiverBatteryLevel(Float receiverBatteryLevel) {
        this.receiverBatteryLevel = receiverBatteryLevel;
    }

    public Float getSenderBTVersion() {
        return senderBTVersion;
    }

    public void setSenderBTVersion(Float senderBTVersion) {
        this.senderBTVersion = senderBTVersion;
    }

    public Float getSenderTemp() {
        return senderTemp;
    }

    public void setSenderTemp(Float senderTemp) {
        this.senderTemp = senderTemp;
    }

    public Float getReceiverTemp() {
        return receiverTemp;
    }

    public void setReceiverTemp(Float receiverTemp) {
        this.receiverTemp = receiverTemp;
    }

    public Float getReceiverBTVersion() {
        return receiverBTVersion;
    }

    public void setReceiverBTVersion(Float receiverBTVersion) {
        this.receiverBTVersion = receiverBTVersion;
    }

    public Float getRssi() {
        return rssi;
    }

    public void setRssi(Float rssi) {
        this.rssi = rssi;
    }
}

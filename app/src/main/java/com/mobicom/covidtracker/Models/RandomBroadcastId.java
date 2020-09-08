package com.mobicom.covidtracker.Models;

public class RandomBroadcastId {

    private String broadcastKey;
    private int secretId;

    public RandomBroadcastId(String broadcastKey, int secretId) {
        this.broadcastKey = broadcastKey;
        this.secretId = secretId;
    }

    public String getBroadcastKey() {
        return broadcastKey;
    }

    public void setBroadcastKey(String broadcastKey) {
        this.broadcastKey = broadcastKey;
    }

    public int getSecretId() {
        return secretId;
    }

    public void setSecretId(int secretId) {
        this.secretId = secretId;
    }
}

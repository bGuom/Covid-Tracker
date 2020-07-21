package com.mobicom.covidtracker.Models;

public class SecretKeys {

    public String broadcastSecret;
    public String idSecret1;
    public String idSecret2;
    public String idSecret3;
    public String idSecret4;

    public SecretKeys(){}

    public SecretKeys(String broadcastSecret, String idSecret1, String idSecret2, String idSecret3, String idSecret4) {
        this.broadcastSecret = broadcastSecret;
        this.idSecret1 = idSecret1;
        this.idSecret2 = idSecret2;
        this.idSecret3 = idSecret3;
        this.idSecret4 = idSecret4;
    }

    public String getBroadcastSecret() {
        return broadcastSecret;
    }

    public void setBroadcastSecret(String broadcastSecret) {
        this.broadcastSecret = broadcastSecret;
    }

    public String getIdSecret1() {
        return idSecret1;
    }

    public void setIdSecret1(String idSecret1) {
        this.idSecret1 = idSecret1;
    }

    public String getIdSecret2() {
        return idSecret2;
    }

    public void setIdSecret2(String idSecret2) {
        this.idSecret2 = idSecret2;
    }

    public String getIdSecret3() {
        return idSecret3;
    }

    public void setIdSecret3(String idSecret3) {
        this.idSecret3 = idSecret3;
    }

    public String getIdSecret4() {
        return idSecret4;
    }

    public void setIdSecret4(String idSecret4) {
        this.idSecret4 = idSecret4;
    }
}

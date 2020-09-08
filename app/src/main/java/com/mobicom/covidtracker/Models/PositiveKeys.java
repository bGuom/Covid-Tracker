package com.mobicom.covidtracker.Models;

import java.util.ArrayList;

public class PositiveKeys {
    public ArrayList<String> keys;

    public PositiveKeys(ArrayList<String> keys) {
        this.keys = keys;
    }

    public PositiveKeys() {
    }

    public ArrayList<String> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<String> keys) {
        this.keys = keys;
    }
}

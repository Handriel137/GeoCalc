package com.example.geocalc;

import org.parceler.Parcel;

@Parcel
public class LocationLookup {
    String _key;
    double origLat;
    double origLng;
    double endLat;
    double endLng;


    String timestamp;

    public String toString() {
        return this._key + "(" + this.origLat + ", " + this.origLng + ")" + " (" + this.endLat + ", " + this.endLng + ")";
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getOrigLat() {
        return origLat;
    }

    public void setOrigLat(double origLat) {
        this.origLat = origLat;
    }

    public double getOrigLng() {
        return origLng;
    }

    public void setOrigLng(double origLng) {
        this.origLng = origLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }
}
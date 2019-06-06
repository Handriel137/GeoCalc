package com.example.geocalc;

import org.parceler.Parcel;

@Parcel
public class LocationLookup {
    double origLat;
    double origLng;
    double endLat;
    double endLng;

    public String toString() {
        return "(" + this.origLat + ", " + this.origLng + ")" + " (" + this.endLat + ", " + this.endLng + ")";
    }
}
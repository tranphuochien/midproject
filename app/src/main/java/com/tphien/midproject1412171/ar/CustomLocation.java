package com.tphien.midproject1412171.ar;

/**
 * Created by Tran Phuoc Hien on 4/9/2017.
 */

public class CustomLocation {
    public String status;
    public Data[] data;

    CustomLocation() {
        data = new Data[1];

    }

    public CustomLocation(Data [] data) {
        this.data = data;
    }
}
class Data {
    String name ="";
    Double lat = 0d;
    Double lon = 0d;
    String img = "";

    public Data(String name, Double lat, Double lon, String img) {
        this.name = name;
        this.lat = lat;
        this.lon= lon;
        this.img = img;
    }
}

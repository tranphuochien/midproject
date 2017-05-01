package com.tphien.midproject1412171.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Tran Phuoc Hien on 1/5/2016.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}

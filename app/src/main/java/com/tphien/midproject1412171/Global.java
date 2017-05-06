/**
 * Created by OP on 5/3/2017.
 */

package com.tphien.midproject1412171;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.tphien.midproject1412171.Modal.Restaurant;

import java.util.ArrayList;


public class Global extends Application{
    public static int radius = 1000;
    private static LatLng fakeLatLng = new LatLng(10.8483638,106.664746); // Thống nhất , Gò vấp
    private static LatLng realLatLng = new LatLng(10.8483638,106.664746);
    private static ArrayList<Restaurant> dataBank = new ArrayList<>();
    private static ArrayList<Restaurant> dataFavorites = new ArrayList<>();

    //mode = true: real Position
    //mode = false: fake Position
    public static LatLng getCurPosition(boolean mode) {
        if (mode)
            return realLatLng;
        return fakeLatLng;
    }

    public static void updateCurPosition(double lat, double lon, boolean mode) {
        if (mode) {
            realLatLng = new LatLng(lat, lon);
        }
        fakeLatLng = new LatLng(lat, lon);
    }

    public static ArrayList<Restaurant> getDataBank() {
        return dataBank;
    }

    public static Restaurant getDataByIndex(int idx) {
        if (idx < 0)
            return new Restaurant();
        return dataBank.get(idx);
    }

    public static void setDataBank(ArrayList<Restaurant> dataBank) {
        Global.dataBank = dataBank;
    }

    public static void addNewFavorite(Restaurant restaurant) {
        dataFavorites.add(restaurant);
    }

    public static ArrayList<Restaurant> getDataFavorites() {
        return dataFavorites;
    }
}

/**
 * Created by OP on 5/3/2017.
 */

package com.tphien.midproject1412171;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.tphien.midproject1412171.Modal.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Global extends Application{
    public static int radius = 1000;
    private static LatLng fakeLatLng = new LatLng(10.8483638,106.664746); // Thống nhất , Gò vấp
    private static LatLng realLatLng;
    private static boolean mode;
    private static ArrayList<Restaurant> dataBank = new ArrayList<>();
    private static HashMap<String, Restaurant> dataFavorites = new HashMap<>();
    public static Context tmpContext;

    public static void setRealPosition(LatLng position){
        realLatLng = position;
    }

    public static void setMode(boolean setting){
        mode = setting;
    }

    //mode = true: real Position
    //mode = false: fake Position
    public static LatLng getCurPosition() {
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

    public static boolean checkFavourite(Restaurant restaurant){
        if (dataFavorites.get(restaurant.getName()) == null)
            return false;
        return true;
    }

    public static void setDataBank(ArrayList<Restaurant> dataBank) {
        Global.dataBank = dataBank;
    }

    public static void addNewFavorite(Restaurant restaurant) {
        dataFavorites.put(restaurant.getName(), restaurant);
    }

    public static void removeFavorite(Restaurant restaurant){
        dataFavorites.remove(restaurant.getName());
    }

    public static ArrayList<Restaurant> getDataFavorites() {
        ArrayList<Restaurant> list = new ArrayList<>();

        for(Map.Entry<String, Restaurant> entry: dataFavorites.entrySet()){
            list.add(entry.getValue());
        }

        return list;
    }
}

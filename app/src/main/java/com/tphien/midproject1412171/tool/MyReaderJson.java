/**
 * Created by Tran Phuoc Hien on 5/1/2017.
 */
package com.tphien.midproject1412171.tool;

import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyReaderJson {
    /*
     * This matches only once in whole input,
     * so Scanner.next returns whole InputStream as a String.
     * http://stackoverflow.com/a/5445161/2183804
     */
    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

    public ArrayList<Restaurant> read(InputStream inputStream) throws JSONException {
        ArrayList<Restaurant> items = new ArrayList<Restaurant>();
        String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
        JSONArray array = new JSONArray(json);

        int []idAvatars = {R.drawable.avatar, R.drawable.avatar2, R.drawable.avatar3};

        int n = array.length();
        String name = "";
        String  address = "";
        String linkWebsite = "";
        String phoneNumber = "";
        String email = "";
        int rating = 0;
        String review = "";
        String url = "";
        double lat = 0f;
        double lon = 0f;

        for (int i = 0; i < n; i++) {
            JSONObject object = array.getJSONObject(i);

            if (!object.isNull("name")) {
                name = object.getString("name");
            }
            if (!object.isNull("address")) {
                address = object.getString("address");
            }
            if (!object.isNull("website")) {
                linkWebsite = object.getString("website");
            }
            if (!object.isNull("phone")) {
                phoneNumber = object.getString("phone");
            }
            if (!object.isNull("email")) {
                email = object.getString("email");
            }
            if (!object.isNull("rating")) {
                rating = object.getInt("rating");
            }
            if (!object.isNull("rating")) {
                rating = object.getInt("rating");
            }
            if (!object.isNull("reviews")) {
                review = object.getString("reviews");
            }
            if (!object.isNull("url")) {
                url = object.getString("url");
            }
            if (!object.isNull("lat")) {
                lat = object.getDouble("lat");
            }
            if (!object.isNull("lon")) {
                lon = object.getDouble("lon");
            }

            items.add(new Restaurant(name,address, linkWebsite, phoneNumber, email, rating, review,
                    url, lat, lon, idAvatars));
        }
        return items;
    }
}

/**
 * Created by tranphuochien on 4/29/2017.
 */

package com.tphien.midproject1412171.Modal;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.tphien.midproject1412171.R;

import java.io.Serializable;
import java.util.Random;

public class Restaurant implements ClusterItem, Serializable {
    //Name,Address,Website,Phone,Email,Rating,Reviews,URL
    private String NO_INFO ="No info";

    private String name = NO_INFO;
    private String  address = NO_INFO;
    private String linkWebsite = NO_INFO;
    private String phoneNumber = NO_INFO;
    private String email = NO_INFO;
    private int rating = 0;
    private String review = NO_INFO;
    private String url = NO_INFO;
    private double lat = 0f;
    private double lon = 0f;

    int []idAvatars = {R.drawable.avatar, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
            R.drawable.avatar7};

    private int curPosAvatar = 0;
    private Random r = new Random();

    public Restaurant(String name, String address, int[] idAvatars) {
        this.name = name;
        this.address = address;
        this.idAvatars = idAvatars;
    }

    public Restaurant() {}

    public Restaurant(String name, String address, String website, String phone, String email,
                      int rating, String review, String url, double lat, double lon, int[] idAvatars) {
        this.name = name;
        if (address.length() > 0) {this.address = address;}
        if (website.length() > 0) {this.linkWebsite = website;}
        this.phoneNumber = phone;
        if (email.length() > 0) {this.email = email; }
        this.rating = rating;
        if (review.length() > 0) {this.review = review;}
        if (url.length() > 0) {this.url= url;}
        this.lat = lat;
        this.lon = lon;
        this.idAvatars = idAvatars;
        this.curPosAvatar = r.nextInt(7);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkWebsite() {
        return linkWebsite;
    }

    public void setLinkWebsite(String linkWebsite) {
        this.linkWebsite = linkWebsite;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int[] getIdAvatars() {
        return idAvatars;
    }

    public void setIdAvatars(int[] idAvatars) {
        this.idAvatars = idAvatars;
    }

    public int getCurPosAvatar() {
        return curPosAvatar;
    }

    public void setCurPosAvatar(int curPosAvatar) {
        this.curPosAvatar = curPosAvatar;
    }

    public int getCurAvatar() {return idAvatars[curPosAvatar];}

    public String getStringResourceCurAvatar() {
        switch (curPosAvatar) {
            case 0:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar";
            case 1:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar2";
            case 2:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar3";
            case 3:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar4";
            case 4:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar5";
            case 5:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar6";
            case 6:
                return "android.resource://com.tphien.midproject1412171/drawable/avatar7";
        }
        return null;
    }
    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lon);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

/**
 * Created by tranphuochien on 4/29/2017.
 */

package com.tphien.midproject1412171.Modal;

import android.graphics.Bitmap;



public class Restaurant {
    private String name = "";
    private String  address = "";
    private int     rate = 0;
    private String phoneNumber = "";
    private String email = "";
    private String linkWebsite = "";
    private double lat = 0f;
    private double lon = 0f;
    private int[] idAvatars = new int[0];
    private int curPosAvatar = 0;
    private String description = "";

    public Restaurant(String name, String address, int[] idAvatars) {
        this.name = name;
        this.address = address;
        this.idAvatars = idAvatars;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
}

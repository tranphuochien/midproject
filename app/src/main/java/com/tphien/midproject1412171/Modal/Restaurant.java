/**
 * Created by tranphuochien on 4/29/2017.
 */

package com.tphien.midproject1412171.Modal;


public class Restaurant {
    //Name,Address,Website,Phone,Email,Rating,Reviews,URL

    private String name = "";
    private String  address = "";
    private String linkWebsite = "";
    private String phoneNumber = "";
    private String email = "";
    private int rating = 0;
    private String review = "";
    private String url = "";
    private double lat = 0f;
    private double lon = 0f;

    private int[] idAvatars = new int[0];
    private int curPosAvatar = 0;

    public Restaurant(String name, String address, int[] idAvatars) {
        this.name = name;
        this.address = address;
        this.idAvatars = idAvatars;
    }

    public Restaurant(String name, String address, String website, String phone, String email,
                      int rating, String review, String url, double lat, double lon, int[] idAvatars) {
        this.name = name;
        this.address = address;
        this.linkWebsite = website;
        this.phoneNumber = phone;
        this.email = email;
        this.rating = rating;
        this.review = review;
        this.url= url;
        this.lat = lat;
        this.lon = lon;
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
}

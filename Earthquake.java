package com.example.earthquakeapp.Model;

public class Earthquake
{
    String place;
    String detaillinl;
    String type;
    double Magnitude;
    double lat;
    double lon;
    long time;


    public Earthquake() {

    }

    public Earthquake(String place, String detaillinl, String type, double magnitude, double lat, double lon, long time) {
        this.place = place;
        this.detaillinl = detaillinl;
        this.type = type;
        Magnitude = magnitude;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDetaillinl() {
        return detaillinl;
    }

    public void setDetaillinl(String detaillinl) {
        this.detaillinl = detaillinl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMagnitude() {
        return Magnitude;
    }

    public void setMagnitude(double magnitude) {
        Magnitude = magnitude;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

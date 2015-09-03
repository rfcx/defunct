package org.rfcx.cellmapping.model;


public class Poi {

    private String sid, guid;
    private double lat, lng, accuracy;
    private int signalstrenth, evoDbm, cdmaDbm;
    private String name;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getSignalstrenth() {
        return signalstrenth;
    }

    public void setSignalstrenth(int signalstrenth) {
        this.signalstrenth = signalstrenth;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEvoDbm() {
        return evoDbm;
    }

    public void setEvoDbm(int evoDbm) {
        this.evoDbm = evoDbm;
    }

    public int getCdmaDbm() {
        return cdmaDbm;
    }

    public void setCdmaDbm(int cdmaDbm) {
        this.cdmaDbm = cdmaDbm;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}

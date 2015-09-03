package org.rfcx.cellmapping.model;

/**
 * Created by Urucas on 9/23/14.
 */
public class User {

    private String sid;
    private String name, guid, carrier;

    public User(String name, String guid) {
        this.name = name;
        this.guid = guid;
    }

    public User(String name, String sid, String guid){
        this.name = name;
        this.sid = sid;
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGUID() {
        return guid;
    }

    public void setGUID(String guid) {
        this.guid = guid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
}

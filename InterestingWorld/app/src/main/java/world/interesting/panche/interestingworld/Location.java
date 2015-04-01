package world.interesting.panche.interestingworld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panche on 31/03/2015.
 */
public class Location {
    String id;
    String name;
    String description;
    String url;
    String user;
    String id_user;
    String lat;
    String lng;

    public Location(String Nid,String Nname,String Ndescription,String Nurl,String Nuser,String Nid_user,String Nlat,String Nlng)
    {
        id=Nid;
        name=Nname;
        description=Ndescription;
        url=Nurl;
        user=Nuser;
        id_user=Nid_user;
        lat=Nlat;
        lng=Nlng;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

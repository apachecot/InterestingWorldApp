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
    String lastname;
    String id_user;
    String photo_user;
    String lat;
    String lng;
    String address;
    String country;
    String locality;
    String rating;

    public Location(String Nid,String Nname,String Ndescription,String Nurl,
                    String Nuser,String Nlastname,String Nid_user,String Nphoto_user,String Nlat,String Nlng,
                    String Naddress, String Ncountry, String Nlocality,String Nrating)
    {
        id=Nid;
        name=Nname;
        description=Ndescription;
        url=Nurl;
        user=Nuser;
        lastname=Nlastname;
        id_user=Nid_user;
        lat=Nlat;
        lng=Nlng;
        address=Naddress;
        country=Ncountry;
        locality=Nlocality;
        rating=Nrating;
        photo_user=Nphoto_user;


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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getPhoto_user() {
        return photo_user;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhoto_user(String photo_user) {
        this.photo_user = photo_user;
    }
}

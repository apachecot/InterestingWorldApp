package world.interesting.panche.interestingworld;

/**
 * Created by Panche on 31/03/2015.
 */
public class Comments {
    String id;
    String id_location;
    String id_user;
    String user_name;
    String url;
    String comment;
    String date;


    public Comments(String Nid, String Nid_location, String Nid_user,String Nname, String Nurl, String Ncomment, String Ndate)
    {
        id=Nid;
        id_location=Nid_location;
        url=Nurl;
        user_name=Nname;
        id_user=Nid_user;
        comment=Ncomment;
        date=Ndate;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_location() {
        return id_location;
    }

    public void setId_location(String id_location) {
        this.id_location = id_location;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

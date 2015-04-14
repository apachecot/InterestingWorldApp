package world.interesting.panche.interestingworld;

/**
 * Created by Panche on 31/03/2015.
 */
public class User {
    String id;
    String name;
    String last_name;
    String url;
    String email;


    public User(String Nid, String Nname, String Nlast_name, String Nurl, String Nemail) {
        id = Nid;
        name = Nname;
        url = Nurl;
        last_name = Nlast_name;
        email = Nemail;

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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
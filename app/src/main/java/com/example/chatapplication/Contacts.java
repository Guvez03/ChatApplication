package com.example.chatapplication;

public class Contacts  {
    private String username , userstatus , userimageurl;

    public Contacts(){


    }

    public Contacts(String username, String userstatus, String userimageurl) {
        this.username = username;
        this.userstatus = userstatus;
        this.userimageurl = userimageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getUserimageurl() {
        return userimageurl;
    }

    public void setUserimageurl(String userimageurl) {
        this.userimageurl = userimageurl;
    }
}

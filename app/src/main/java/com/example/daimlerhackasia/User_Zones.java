package com.example.daimlerhackasia;

public class User_Zones {
    String uid,imgid,imgurl;

    public User_Zones(String uid, String imgid, String imgurl) {
        this.uid = uid;
        this.imgid = imgid;
        this.imgurl = imgurl;
    }

    public User_Zones() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}

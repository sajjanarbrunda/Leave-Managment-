package com.example.leavemngt;

public class Data {
    String name , email , id, loct , img , role , date , dep ;


    public Data() {
    }

    public Data(String name, String email, String idl, String loct, String img, String role, String date, String dep ) {
        this.name = name;
        this.email = email;
        this.id = idl;
        this.loct = loct;
        this.img = img;
        this.role = role;
        this.date = date;
        this.dep = dep;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String idl) {
        this.id = id;
    }

    public String getLoct() {
        return loct;
    }

    public void setLoct(String loct) {
        this.loct = loct;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDep() {
        return dep;
    }



    public void setDep(String dep) {
        this.dep = dep;
    }
}

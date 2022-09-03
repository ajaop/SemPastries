package com.example.sempastries;

public class StateClass {

    public StateClass(){}

    public String name, name1, url;

    public boolean unCheck;
    public int id;
    public String id1;

    public StateClass(String name, String name1, boolean unCheck, int id) {
        this.name1 = name1;
        this.name = name;
        this.unCheck = unCheck;
        this.id = id;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnCheck(boolean unCheck) {
        this.unCheck = unCheck;
    }

    public boolean getUncheck() {
        return unCheck;
    }

    public int getId() {
        return id;
    }

    public String getId1() {
        return id1;
    }


    public void setUrl(String url){
        this.url = url; }

    public String getUrl(){
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId1(String id) {
        this.id1 = id;
    }
}

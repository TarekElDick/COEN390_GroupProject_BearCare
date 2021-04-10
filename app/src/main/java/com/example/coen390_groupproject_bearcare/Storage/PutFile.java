package com.example.coen390_groupproject_bearcare.Storage;

public class PutFile {

    public String name;
    public String url;

    public PutFile()
    {

    }

    public PutFile(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package com.example.coen390_groupproject_bearcare.Storage;

public class UploadFile {

    public String name,url;


    public UploadFile()                                                                             // default non-parametrized constructor
    {

    }

    public UploadFile(String name,String url){                                                      // overloaded constructor
        if(name.trim().equals("")) {
            name = "No Name";
        }
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

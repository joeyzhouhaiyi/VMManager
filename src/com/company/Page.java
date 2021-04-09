package com.company;

public class Page {
    public String id;
    public int value;
    public int lastAccessTime;

    public Page(String id,int value, int lat)
    {
        this.id = id;
        this.value = value;
        this.lastAccessTime = lat;
    }
}

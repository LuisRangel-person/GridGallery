package com.kiwi.gridgallery;

import java.util.ArrayList;

public class ListCache {
    private static ListCache INSTANCE;
    private ArrayList<ImageObject> list;
    public static ListCache getInstance(){
        if(INSTANCE == null){
           INSTANCE = new ListCache();
        }
        return INSTANCE;
    }

    public void setList(ArrayList<ImageObject> list) {
        this.list = list;
    }

    public ArrayList<ImageObject> getList(){
        return this.list;
    }
}//List Cache

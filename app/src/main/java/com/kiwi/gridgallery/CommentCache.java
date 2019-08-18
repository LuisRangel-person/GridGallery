package com.kiwi.gridgallery;

import android.util.LruCache;

public class CommentCache extends LruCache<String, String>{
    private static CommentCache INSTANCE;
    //This cache will keep the comments in memory
    // This was originally written in Kotlin, I wrote it in java here to keep things consistent with the rest of the app
    private LruCache<String, String> memoryCache;

    public CommentCache(int maxSize){
        super(maxSize);
    }//Init

    public static CommentCache getInstance(){//This is a singleton implementation so this can be accessed in different activities
        if(INSTANCE == null){
            INSTANCE = new CommentCache(getCacheSize());
        }
        return INSTANCE;
    }

    public static int getCacheSize(){
        int maxMem = (int) (Runtime.getRuntime().maxMemory() / 1024);
        return maxMem / 8;
    }//getCacheSize

    public String getString(String key){
        return this.get(key);
    }//getString

    public void putString(String key, String value){
        if(!getString(key).isEmpty()){
            this.put(key, value);
        }//Make sure the key isn't empty
    }

}

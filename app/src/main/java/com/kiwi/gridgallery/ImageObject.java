package com.kiwi.gridgallery;

import org.json.JSONObject;

class ImageObject{

    private String title;
    private String description;
    private String opUser;//Original Poster
    private String imageLink;//Link to the image
    private Boolean nsfw;//Is the image safe for work?
    public ImageObject(JSONObject json){
        title = json.optString("title");
        description = json.optString("description");
        opUser = json.optString("account_url");
        nsfw = json.optBoolean("nsfw");
        try {
            imageLink = json.optJSONArray("images").getJSONObject(0).optString("link");//SO this is a bit complicated as some entries have an array of images
        }
        catch (Exception e){
            imageLink = json.optString("link");//If there is no images array, then the link will contain the image
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOpUser() {
        return opUser;
    }

    public String getImageLink() {
        return imageLink;
    }

    public Boolean getNsfw() {
        return nsfw;
    }
}

package com.kiwi.gridgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    GalleryAdapter adapter;
    EditText searchQuery;
    String baseURL = " https://api.imgur.com/3/gallery/search/1?q=";//q= is the query, this will be retrieved from the text input bar
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CommentCache cache = CommentCache.getInstance();

        searchQuery = findViewById(R.id.search_query);
        query = searchQuery.getText().toString().isEmpty() ? "kiwi" : searchQuery.getText().toString();
        new JSONLoaderTask().execute(baseURL + query);
    }

    class JSONLoaderTask extends AsyncTask<String, String, String>{//I wrote this function for another project, had to write it in Java this time
        @Override
        protected String doInBackground(String... strings) {
            try {//Load up the URL and attach the auth header
                String authHeaderID = "137cda6b5008a7c";
                URL url = new URL(strings[0]);
                HttpURLConnection imageConnection =  (HttpURLConnection) url.openConnection();
                imageConnection.addRequestProperty("Authorization", "Client-ID " + authHeaderID);
                imageConnection.connect();
                String r = imageConnection.getResponseMessage();
                int status = imageConnection.getResponseCode();//Getting the Status and handling it
                if(status != 0){
                    BufferedReader buff = new BufferedReader(new InputStreamReader(imageConnection.getInputStream()));
                    StringBuilder strBul = new StringBuilder();
                    String line;
                    while ((line = buff.readLine()) != null){
                        strBul.append(line + "\n");
                    }
                    buff.close();
                    return strBul.toString();
                }//Status Equals 200
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }//Do In Background

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject response = new JSONObject(s);
                JSONArray JSONarr = response.getJSONArray("data");
                ArrayList<ImageObject> list = new ArrayList<>();
                for(int i = 0; i < JSONarr.length();i++){
                    ImageObject current = new ImageObject(JSONarr.optJSONObject(i));
                    if(current.getImageLink() != null && !current.getImageLink().equals("") && vaildateImage(current.getImageLink())) {//Check if image link isn't null , if it is don't add to the list, also exclude .mp4 as they are no images
                        list.add(current);
                    }
                }//Get Image List
                //Set up the grid view
                RecyclerView recyclerView = findViewById(R.id.gallery);
                int numColumns = 5;
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),numColumns));
                adapter = new GalleryAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
//                Picasso.with(getApplication()).load(list.get(0).getImageLink()).into(test);
                int stop = 0;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }//JSON Loader Class
    private boolean vaildateImage(String url){
        if(url.contains(".mp4")){
            return false;
        }
        if(url.contains(".gif")){
            return false;
        }
        return true;
    }

    public void imageSearch(View target){//This will query the Imgur API when pressed
        query = searchQuery.getText().toString();
        if(!query.isEmpty()) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            new JSONLoaderTask().execute(baseURL + query);
        }
    }

}

package com.kiwi.gridgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    RecyclerView gallery;
    ImageView error_no_internet;
    ImageView error_no_results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        error_no_internet = findViewById(R.id.error_no_internet);
        error_no_results = findViewById(R.id.error_no_results);
        gallery = findViewById(R.id.gallery);
        searchQuery = findViewById(R.id.search_query);
        if(isConnected()) {
            error_no_internet.setVisibility(View.GONE);
            gallery.setVisibility(View.VISIBLE);
            ListCache listCache = ListCache.getInstance();
            if(listCache.getList() == null) {//If a list exists, don't reload
                query = searchQuery.getText().toString().isEmpty() ? "kiwi" : searchQuery.getText().toString();
                new JSONLoaderTask().execute(baseURL + query);
            }
        }
        else {//Show an error message if there is no internet connectivity
            error_no_internet.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.GONE);
            new CustomDialog(this, "Error", "Not connected to the internet.", true, false);
        }
    }

    @Override
    protected void onResume(){//This is a view model to hold the json object list and then set up the grid view
        super.onResume();
        ListCache listCache = ListCache.getInstance();
        ArrayList<ImageObject> list = listCache.getList();
        setUpGridView(list);
    }

    class JSONLoaderTask extends AsyncTask<String, String, String>{//I wrote this function for another project, had to write it in Java this time
        @Override
        protected String doInBackground(String... strings) {
            try {//Load up the URL and attach the auth header
                String authHeaderID = "137cda6b5008a7c";
                URL url = new URL(strings[0]);
                HttpURLConnection imageConnection =  (HttpURLConnection) url.openConnection();
                imageConnection.addRequestProperty("Authorization", "Client-ID " + authHeaderID);//Add the client ID for the API call
                imageConnection.connect();
                String r = imageConnection.getResponseMessage();
                int status = imageConnection.getResponseCode();//Getting the Status and handling it
                if(status == 200){
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
                gallery.setVisibility(View.VISIBLE);//Reveal the Gallery
                JSONObject response = new JSONObject(s);
                JSONArray JSONarr = response.getJSONArray("data");
                ArrayList<ImageObject> list = new ArrayList<>();

                //Iterate though the JSON array to build Image Objects
                for(int i = 0; i < JSONarr.length();i++){
                    ImageObject current = new ImageObject(JSONarr.optJSONObject(i));
                    if(current.getImageLink() != null && !current.getImageLink().equals("") && validateImage(current.getImageLink())) {//Check if image link isn't null , if it is don't add to the list, also exclude .mp4 as they are no images
                        list.add(current);
                    }
                }//Get Image List

                ListCache listCache = ListCache.getInstance();
                listCache.setList(list);
                setUpGridView(list);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }//JSON Loader Class

    private boolean validateImage(String url){
        if(url.contains(".mp4")){
            return false;
        }
        if(url.contains(".gif")){
            return false;
        }
        return true;
    }//Validate Image

    public void imageSearch(View target){//This will query the Imgur API when pressed
        if(searchQuery != null) {
            query = searchQuery.getText().toString();
        }
        if(isConnected()) {
            if(query != null && !query.isEmpty()) {
                error_no_internet.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                new JSONLoaderTask().execute(baseURL + query);
            }
        }
        else {//Show an error message if there is no internet connectivity
            error_no_internet.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.GONE);
            //CustomDialog(this, "Error", "Not connected to the internet.", true, false)
        }
    }

    public void setUpGridView(ArrayList<ImageObject> list){
        //Set up the grid view
        RecyclerView recyclerView = findViewById(R.id.gallery);
        int numColumns = 5;
        if(list != null) {
            if(list.size() == 0){
                gallery.setVisibility(View.GONE);
                error_no_results.setVisibility(View.VISIBLE);
            }
            else {
                error_no_results.setVisibility(View.GONE);//Show an image that lets the user know there was no results
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numColumns));
                adapter = new GalleryAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
            }
        }
    }//Set Up Grid View

    public boolean isConnected(){//This checks if a phone is connected to the internet
        //Check if there is internet, can't load nothin' if there isn't any internet
        ConnectivityManager connectManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
        return networkInfo.isConnectedOrConnecting();
    }
}

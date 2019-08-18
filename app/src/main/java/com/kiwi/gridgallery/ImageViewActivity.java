package com.kiwi.gridgallery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    TextView imageviewTitle;
    TextView imageViewOP;
    TextView imageDescription;
    ImageView imageviewImage;
    EditText imageviewComment;
    String comment;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        CommentCache cache = CommentCache.getInstance();
        //Get the info from the grid view
        String title = getIntent().getStringExtra("image_title");
        imageUrl = getIntent().getStringExtra("image_url");
        String opName = getIntent().getStringExtra("image_op");
        String description = getIntent().getStringExtra("image_description");
        description = description.equals("null") ? "No description given" : description;//The imgur api sends a string that reads "null" instead of actual null, don't want that showing up
        setTitle(title);

        //Making the back button exist
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //Set up UI
        imageviewImage = findViewById(R.id.imageview_image);
        Picasso.with(this).load(imageUrl).into(imageviewImage);

        imageviewTitle = findViewById(R.id.imageview_title);
        imageviewTitle.setText(title);

        imageViewOP = findViewById(R.id.imageview_op);
        imageViewOP.setText(opName);

        imageDescription = findViewById(R.id.imageview_description);
        imageDescription.setText(description);
        //Check if the comment is in the cache and draw
        imageviewComment = findViewById(R.id.imageview_comment);
        comment = cache.get(imageUrl);//This gets the comment from the cache, the key used for it is the imageURL
        if(comment != null){//If the string is found in the cache, then the edit text will be populated by the comment
            imageviewComment.setText(comment);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){//This handles the pressing of the back button
        finish();
        return true;
    }

    public void submitComment(View target){//This will handle the submission of image comments
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        comment = imageviewComment.getText().toString();
        CommentCache.getInstance().put(imageUrl, comment);
    }
}

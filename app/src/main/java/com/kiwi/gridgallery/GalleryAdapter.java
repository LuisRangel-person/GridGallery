package com.kiwi.gridgallery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<ImageObject> images;
    private LayoutInflater inflater;
    private ImageClickListener clickListener;
    private Context con;

    GalleryAdapter(Context context, ArrayList<ImageObject> data){
        this.inflater = LayoutInflater.from(context);
        this.images = data;
        this.con = context;
    }

    //Get the Grid Layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_gallerygrid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageObj = images.get(position);
        if(images.get(position).getNsfw()){//Don't want NSFW stuff to just show up on the gallery view
            holder.image.setImageDrawable(con.getDrawable(R.drawable.grid_nsfw));
        }
        else {
            Picasso.with(con).load(images.get(position).getImageLink()).resize(80, 80).centerCrop().into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        ImageObject imageObj;

        ViewHolder(View imageView) {
            super(imageView);
            image = imageView.findViewById(R.id.grid_image);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(con, ImageViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("image_title", imageObj.getTitle());
            intent.putExtra("image_url", imageObj.getImageLink());
            intent.putExtra("image_op", imageObj.getOpUser());
            intent.putExtra("image_description", imageObj.getDescription());
            con.startActivity(intent);
            Log.i("Grid Click", "Clicked");
        }
    }


    // parent activity will implement this method to respond to click events
    public interface ImageClickListener {
        void onItemClick(View view, int position);
    }
}

package com.kiwi.gridgallery;

import android.content.Context;
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
        holder.imageURL = images.get(position).getImageLink();
        Picasso.with(con).load(images.get(position).getImageLink()).resize(80, 80).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        String imageURL;

        ViewHolder(View imageView) {
            super(imageView);
            image = imageView.findViewById(R.id.grid_image);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ImageClickListener imageClickListener) {
        this.clickListener = imageClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ImageClickListener {
        void onItemClick(View view, int position);
    }
}

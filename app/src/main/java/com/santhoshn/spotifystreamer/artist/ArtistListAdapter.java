package com.santhoshn.spotifystreamer.artist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.santhoshn.spotifystreamer.R;
import com.squareup.picasso.Picasso;
/**
 * Created by santhosh on 30/06/15.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist> {

    private Context context;
    private int layoutResourceId;
    private Artist artists[] = null;

    public ArtistListAdapter(Context context, int layoutResourceId, Artist[] artists) {
        super(context, layoutResourceId, artists);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View rowView = view;
        ArtistHolder holder = null;
        //view can be empty, in that case, build one
        if(rowView == null) {
            //inflate the Artist List Item View
            LayoutInflater inflater= ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.artist_list_item, parent, false);

            //Create a Brand New ArtistHolder Object
            holder = new ArtistHolder();
            //find the TextView and ImageView and assign to corresponding objects in the holder
            holder.artistNameTextView = (TextView) rowView.findViewById(R.id.artist_name);
            holder.artistThumbnailImgView = (ImageView) rowView.findViewById(R.id.artist_thumbnail);
            rowView.setTag(holder);
        } else {
            //since view is not null, fetch the holder from the Tag
            holder = (ArtistHolder)rowView.getTag();
        }

        //Lookup the Artist Object at the given position.
        Artist artist = artists[position];
        //Load the image url using the picasso library, which takes care of downloading caching and displaying it in the ImageView.
        Picasso.with(context)
                .load(artist.getThumbnailImageUrl())
                .into(holder.artistThumbnailImgView);

        //Set the Artist Name from the Object in the TextView
        holder.artistNameTextView.setText(artist.getName());
        //return the rowView to show
        return rowView;
    }


    static class ArtistHolder {
        ImageView artistThumbnailImgView;
        TextView artistNameTextView;
    }

}

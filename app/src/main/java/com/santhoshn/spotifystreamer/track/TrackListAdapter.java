package com.santhoshn.spotifystreamer.track;

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
public class TrackListAdapter  extends ArrayAdapter<Track> {

    private Context context;
    private int layoutResourceId;
    private Track tracks[] = null;

    public TrackListAdapter(Context context, int layoutResourceId, Track[] tracks) {
        super(context, layoutResourceId, tracks);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;
        TrackHolder holder = null;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.track_list_item, parent, false);

            //Create a holder object and fill the holder member with right view.
            holder = new TrackHolder();
            holder.albumNameTextView = (TextView)rowView.findViewById(R.id.album_name);
            holder.trackNameTextView = (TextView)rowView.findViewById(R.id.track_name);
            holder.thumbnailImgView = (ImageView)rowView.findViewById(R.id.track_thumbnail);

            rowView.setTag(holder);
        } else {
            holder = (TrackHolder) rowView.getTag();
        }

        //fetch the corresponding Track object at that position.
        Track track = tracks[position];
        //set the values from Track Object to right views.
        holder.albumNameTextView.setText(track.getAlbumName());
        holder.trackNameTextView.setText(track.getTrackName());
        Picasso.with(context).load(track.getThumbnailImageUrl())
                .into(holder.thumbnailImgView);
        return rowView;
    }

    static class TrackHolder {
        ImageView thumbnailImgView;
        TextView  trackNameTextView;
        TextView albumNameTextView;
    }
}
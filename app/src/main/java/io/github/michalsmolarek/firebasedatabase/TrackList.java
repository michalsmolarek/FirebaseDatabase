package io.github.michalsmolarek.firebasedatabase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by michalsmolarek on 20.02.2018.
 */

// Adapter dla listy piosenek, analogicznie jak dla artysty

public class TrackList extends ArrayAdapter<Track> {
    private Activity context;
    List<Track> tracks;

    public TrackList(Activity context, List<Track> tracks) {
        super(context, R.layout.layout_tracks, tracks);
        this.context = context;
        this.tracks = tracks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_tracks, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.trackName);
        TextView textViewRating = (TextView) listViewItem.findViewById(R.id.trackRating);

        Track track = tracks.get(position);
        textViewName.setText(track.getTrackName());
        textViewRating.setText("Ocena: " + String.valueOf(track.getRating()) + " / 5 ");

        return listViewItem;
    }
}

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

// ta klasa to Adapter, czyli taka klasa, która obsłuży listowanie

public class ArtistList extends ArrayAdapter<Artist> {
    private Activity context; // pobieramy sobie kontekst w którym pracujemy
    private List<Artist> artistList; // lista

    public ArtistList(@NonNull Context context, List<Artist> artistList) {
        super(context, R.layout.layout_artist, artistList);
        this.context = (Activity) context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_artist, null, true); // ustawiamy layout dla lsity

        // inicjalizacja widoków
        TextView editTextViewName = (TextView) listViewItem.findViewById(R.id.editTextDisplayName);
        TextView editTextGenre = (TextView) listViewItem.findViewById(R.id.editTextDisplayGenre);

        Artist artist = artistList.get(position); // pobieramy pozycje kontretnego artysty

        editTextViewName.setText(artist.getArtistName()); // wyswietlamy imie artysty
        editTextGenre.setText(artist.getArtistGenre()); // i jego gatunek muzyczny

        return listViewItem; // zwracamy lsitę
    }
}

package io.github.michalsmolarek.firebasedatabase;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    // tworze pola dla widokow
    TextView textViewArtistName;
    EditText editTextNameTrack;
    SeekBar seekBarRating;
    Button buttonAddTrack;
    ListView artistTracks;

    //tworze referencje do bazy danych FIREBASE DATABASE
    DatabaseReference databaseTracks;

    List<Track> tracks; // tworze liste w ktorej przechowam piosenki

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track); // te pola omowilem w aplikacji lotto

        // inicjalizacja
        textViewArtistName = (TextView) findViewById(R.id.textViewArtistName);
        editTextNameTrack = (EditText) findViewById(R.id.editTextNameTrack);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);
        buttonAddTrack = (Button) findViewById(R.id.buttonAddTrack);
        artistTracks = (ListView) findViewById(R.id.artistTracks);

        Intent intent = getIntent(); // pobieramy intencję z innej aktywności

        String id = intent.getStringExtra(MainActivity.ARTIST_ID); // dane na temat ID przechowujemy w zmiennej, wyciągamy ją z intencji
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME); // analogicznie

        textViewArtistName.setText(name); // wyświetlamy

        tracks = new ArrayList<>(); // tworzymy listę

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id); // przypisujemy referencję do zmiennej

        buttonAddTrack.setOnClickListener(new View.OnClickListener() { // obiekt nasluchujacy klikniecia
            @Override
            public void onClick(View view) {
                saveTrack(); // metoda zapisująca
            }
        });



    }

    @Override
    protected void onStart() { // metoda wywoływana przy uruchamianiu aktywności
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() { // metoda nasłuchująca zmian w bazie
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // dataSnapshot to zmienna, która przechowuje dane
                tracks.clear();
                for(DataSnapshot tracksSnapshot : dataSnapshot.getChildren()){
                    Track track = tracksSnapshot.getValue(Track.class); // wyciągamy dane do zmiennej
                    tracks.add(track); // wklepujemy je

                }
                TrackList adapter = new TrackList(AddTrackActivity.this, tracks); // dzięki adapterowi ustawiamy w liście
                artistTracks.setAdapter(adapter);// ustawiamy
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { // metoda nasłuchująca błędów w bazie, może być pusta

            }
        });
    }


    private void saveTrack(){ // metoda zapisująca dane
        String name = editTextNameTrack.getText().toString().trim(); // wyciągamy dane z widoku
        int rating = seekBarRating.getProgress(); // tu tak samo

        if(TextUtils.isEmpty(name)){ // sprawdzamy czy pole nie jest puste, jesli są to pokazujem ywiadomosc
            Toast.makeText(this, "Wprowadź nazwę piosenki", Toast.LENGTH_SHORT).show(); // Toast to taki alert z javascriptu
        } else {

            // jesli dane sa prawidlowe, to wklepujemy do bazy
            String id = databaseTracks.push().getKey();
            Track track = new Track(id, name, rating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this, "Wprowadzono piosenkę", Toast.LENGTH_SHORT).show();
        }
    }
}

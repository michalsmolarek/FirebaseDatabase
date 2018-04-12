package io.github.michalsmolarek.firebasedatabase;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    // tworzymy stale potrzebne do np: intecji
    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID= "artistid";

    // tworzymy pola
    EditText editTextName;
    Spinner spinnerGatunki;
    Button buttonAddArtist;

    ListView listView;

    List<Artist> artistList;

    DatabaseReference databaseAertists; // referencja do bazy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bla bla bla, inicjalizacja
        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGatunki = (Spinner) findViewById(R.id.spinnerGenere);
        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);

        listView = (ListView) findViewById(R.id.listViewArtists);

        artistList = new ArrayList<>();

        databaseAertists = FirebaseDatabase.getInstance().getReference("artists"); // referencja do dziecka

        // obiekt nasluchujacy
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        // gdy klikniemy na konkretny element listy
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get(i); // pobieramy  pozycje tego elementu

                // tworzymy intencje w ktorej wybieramy aktywnosc ktora chcemy uruchomic
                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);
                intent.putExtra(ARTIST_ID, artist.getArtistId()); // wklepujemy dane do intencji
                intent.putExtra(ARTIST_NAME, artist.getArtistName()); // jak wyzej

                startActivity(intent); // startujemy aktywnosc

            }
        });

        // gdy dłuzej przytrzymamy palucha na elemencie listy
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get(i); // pozycja
                showDialogUpdate(artist.getArtistId(), artist.getArtistName()); // pokazujemy okienko dialogowe
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseAertists.addValueEventListener(new ValueEventListener() { // obiekt nasluchujacy zmian w bazie
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                artistList.clear(); // czyscimy liste, taki refresh
                for(DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class); // pobieramy dane zgodnie z modelem
                    artistList.add(artist); // dodajemy
                }

                ArtistList adapter = new ArtistList(MainActivity.this, artistList); // wklepujemy to i miksujemy w liste
                listView.setAdapter(adapter); // ostateczne dopięcie do listy
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // metoda tworząca okienko dialogowe, nie chce mi sie jej opisywac, jest to dobrze zrobione w dokumetacju
    private void showDialogUpdate(final String artistId, String artistName){

        // budowanie okienka
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        builder.setView(dialogView);

        // kilka zmiennych
        final EditText editTextUpdate =  (EditText) dialogView.findViewById(R.id.editTextUpdate);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);
        final Spinner spinnerUpdate = (Spinner) dialogView.findViewById(R.id.spinnerUpdate);

        // tworzenie guzików
        builder.setTitle("Edytuj artystę " + artistName);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextUpdate.getText().toString().trim();
                String genre = spinnerUpdate.getSelectedItem().toString().trim();

                if(TextUtils.isEmpty(name)){
                    editTextUpdate.setError("Nazwa wymagana!");
                    return;
                } else {
                    updateArtist(artistId, name, genre);
                }

                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });


    }

    // usuwanie artysty
    private void deleteArtist(String artistId) {
        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artists").child(artistId);
        DatabaseReference drTrack = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        drArtist.removeValue(); // !!!
        drTrack.removeValue(); // !!!
        Toast.makeText(this, "Artysta został usunięty wraz z jego wszystkimi piosenkami", Toast.LENGTH_LONG).show();

    }

    // update
    public boolean updateArtist(String id, String name, String genre){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist newArtist = new Artist(id, name, genre);
        databaseReference.setValue(newArtist); // !!!
        Toast.makeText(this, "Artysta został zaktualizowany", Toast.LENGTH_SHORT).show();
        return true;
    }

    // dodawanie
    private void addArtist(){
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGatunki.getSelectedItem().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Wprowadź nazwę", Toast.LENGTH_LONG).show();
        } else {
            // jeśli wszystko ok
            String id = databaseAertists.push().getKey(); // stworzy unikalny string jako hasz
            Artist artist = new Artist(id, name, genre); // tworze obiekt - artystę
            databaseAertists.child(id).setValue(artist); // wprowadzi nowego artyste dla id

            Toast.makeText(this, "Wprowadzono artystę", Toast.LENGTH_SHORT).show();
        }
    }
}

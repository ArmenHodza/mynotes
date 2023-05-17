package com.example.mobile_armen_hodza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView notesListView;
    TextView emptyTv;

    static List<String> notes; //shranujemo tukaj note
    static ArrayAdapter adapter; //adapter za polnjenje seznama ListView z beležki (notes)

    SharedPreferences sharedPreferences; //sp za shranjevanje in pridobivanje podatkov

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.mobile_armen_hodza", Context.MODE_PRIVATE);

        notesListView = findViewById(R.id.notes_ListView); //beležka
        emptyTv = findViewById(R.id.emptyTV);

        notes = new ArrayList<>(); //shranujemo tukaj note


        HashSet<String> noteSet = (HashSet<String>) sharedPreferences.getStringSet("notes", null); //S to vrstico se z uporabo funkcije getStringSet pridobi množica HashSet<String> z imenom "notes" iz skupnePreferences. Če niz "notes" ne obstaja, se mu dodeli vrednost null.

        if (noteSet.isEmpty() || noteSet == null){ //če je množica prazna se prikaže element Empty
            emptyTv.setVisibility(View.VISIBLE);
        }else{
            emptyTv.setVisibility(View.GONE);
            notes = new ArrayList<>(noteSet); //v notes se shranijo podatki iz noteSet-a
        }

        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_notes_row, R.id.notesTV, notes); //Adapter je inicializiran z aplikacijskim kontekstom, datoteko postavitve po meri custom_notes_row za vsako vrstico v ListView, ID TextView v postavitvi vrstice (notesTV) in seznamom beležk. Odgovoren za polnjenje seznama ListView s podatki
        notesListView.setAdapter(adapter); //Adapter se nato nastavi na notesListView, da se prikažejo opombe.

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listener če kliknemo na beležko
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class); //intenca za začetek dejavnosti NoteEditorActivity
                intent.putExtra("noteId", position); //intenca posreduje položaj kliknjenega elementa kot dodatek s ključem "noteId"
                startActivity(intent); //intenca se zažene
            }
        });

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //listener na dolgi klik (kliknemo in držimo), na neko baležko
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemToDelete = position; //index pritisnjene beležke
                /*AlertDialog, ki od uporabnika zahteva potrditev izbrisa izbrane opombe.
                Če uporabnik izbere "Da", se opomba odstrani s seznama opomb, adapter je obveščen o spremembi podatkov, posodobljen nabor opomb pa je shranjen v skupnih nastavitvah.
                Če nabor postane prazen ali ničen, se vidljivost emptyTv nastavi na VISIBLE.
                Vračilo true pomeni, da je dogodek dolgega klika porabljen in se ne sme nadaljevati obdelava.*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(itemToDelete);
                                adapter.notifyDataSetChanged();

                                HashSet<String> noteSet = new HashSet<>(notes);
                                sharedPreferences.edit().putStringSet("notes", noteSet).apply();

                                if (noteSet.isEmpty() || noteSet == null){
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            }
                        }).setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu); //MenuInflater napihne postavitev menija add_note_menu.xml(za dodajanje novih beležk) in jo priključi parametru menija
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //ko je iz menu-ja AddNote izbran
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.add_note){
            startActivity(new Intent(getApplicationContext(), NoteEditorActivity.class)); //namera za začetek dejavnosti NoteEditorActivity
            return true;
        }
        return false;
    }
}
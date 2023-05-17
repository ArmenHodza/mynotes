package com.example.mobile_armen_hodza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {

    EditText noteEditText;

    int noteId;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor); //activity_note_editor.xml

        ActionBar actionBar = getSupportActionBar(); //opravilna vrstica

        sharedPreferences = this.getSharedPreferences("com.example.mobile_armen_hodza", Context.MODE_PRIVATE);
        noteEditText = findViewById(R.id.note_EditText);

        Intent intent = getIntent(); //Ta koda pridobi intenco(namero), s katero se je začela dejavnost
        noteId = intent.getIntExtra("noteId", -1); //če ni intence, je default -1(nova beležka)

        if(noteId != -1){ //če ni -1, editamo že obstoječo beležko
            noteEditText.setText(MainActivity.notes.get(noteId)); //v note_EditText se prepiše cela neležka(nota)
            actionBar.setTitle("Edit Note"); //v baru pa se napise EditNote
        }else{ //če je -1, pomeni da je nova
            MainActivity.notes.add(""); //brez naslova
            noteId = MainActivity.notes.size() - 1; //nov id
            actionBar.setTitle("Add Note"); //v baru pa se napise AddNote
        }

        noteEditText.addTextChangedListener(new TextWatcher() {
            /*Ta koda nastavi TextWatcher na besedilo NoteEditText. Ta posluša spremembe v besedilu in ustrezno ukrepa.
            Ko se besedilo spremeni, posodobi ustrezno opombo na seznamu opomb MainActivity z novim besedilom.
            Prav tako obvesti adapter (MainActivity.adapter), da so se podatki spremenili, tako da se lahko posodobi prikaz seznama.
            Poleg tega posodobi skupne nastavitve tako, da seznam opomb pretvori v množico HashSet in jo shrani pod ključem "notes".*/
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notes.set(noteId, String.valueOf(s));
                MainActivity.adapter.notifyDataSetChanged();

                HashSet<String> noteSet = new HashSet<>(MainActivity.notes);
                sharedPreferences.edit().putStringSet("notes", noteSet).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater(); //postavitev menija opredeljeno v save_note_menu.xml,  jo doda parametru menija
        menuInflater.inflate(R.menu.save_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        /*Ta metoda se prikliče, ko je izbran element menija. Preveri, ali ima izbrani element ID R.id.save_note.
        Če je tako, se zažene program MainActivity in konča trenutno dejavnost.
        Tako lahko uporabnik shrani opombo in se vrne v glavno dejavnost.
        Metoda vrne true, kar pomeni, da je bila izbira elementa obdelana.*/
        if(item.getItemId() == R.id.save_note){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
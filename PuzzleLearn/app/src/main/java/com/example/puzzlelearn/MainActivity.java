package com.example.puzzlelearn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class MainActivity extends AppCompatActivity{

    //vars
    private MySQLite myDatabase;
    private ArrayList<Puzzle> allPuzzles=new ArrayList<>();
    private PuzzleAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView puzzleView = findViewById(R.id.recyclerv_view);
        //Permet de bien separer chaque item de la recyclerView
        puzzleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //Permet de positionner correctement l'ensemble des donnees de la liste
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        puzzleView.setLayoutManager(linearLayoutManager);
        puzzleView.setHasFixedSize(true);
        myDatabase = new MySQLite(this);
        allPuzzles = myDatabase.getAllPuzzles();

        //Permet de savoirsi il y a des puzzles dans la bdd
        if(allPuzzles.size() > 0){
            puzzleView.setVisibility(View.VISIBLE);
            //Adapter permet de faire la relation entre les donnees et la recyclerView
            myAdapter = new PuzzleAdapter(this, allPuzzles);
            puzzleView.setAdapter(myAdapter);

        }else {
            puzzleView.setVisibility(View.GONE);
            Toast.makeText(this, "Pas de puzzles, veuillez en ajouter!", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });

    }
    //Boutton en bas à droite, permet d'ajouter un puzzle dans la bdd
    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_image, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.enter_name);
        final EditText noField = (EditText)subView.findViewById(R.id.enter_phno);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Puzzle");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD PUZZLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String nom = nameField.getText().toString();
                final String url = noField.getText().toString();

                if(TextUtils.isEmpty(nom)){
                    Toast.makeText(MainActivity.this, "Réesayer, cela n'a pas fonctionné", Toast.LENGTH_LONG).show();
                }
                else{
                    Puzzle newContact = new Puzzle(nom,url);
                    myDatabase.addPuzzle(newContact);

                    finish();
                    startActivity(getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Tâche annulé", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myDatabase != null){
            myDatabase.close();
        }
    }

    //Menu en haut a droite, permet de chercher un puzzle dans la liste
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem search = menu.findItem(R.id.search_image);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (myAdapter!=null)
                    myAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }






}


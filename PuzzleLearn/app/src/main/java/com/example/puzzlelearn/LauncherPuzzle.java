package com.example.puzzlelearn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
//Acitvité avant de commencer le puzzle, permet de choisir le nombre de pièces
public class LauncherPuzzle extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView textView;
    private String ImageUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_puzzle);
        getIncomingIntent();

        //
        this.seekBar =  findViewById(R.id.seekBar);
        this.textView = findViewById(R.id.textView);
        final int stepSize = 3;
        this.textView.setText("Pieces: " + seekBar.getProgress()  );
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // Quand la seekbar change, valeur de celle ci
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progressValue = (Math.round(progressValue/stepSize))*stepSize;
                seekBar.setProgress(progressValue);
                textView.setText("Pieces: " + progressValue );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    //Recupere l'intent du mainActivity et donc recupere l'url et le nom du puzzle
    private void getIncomingIntent(){
        if(getIntent().hasExtra("image_url") && getIntent().hasExtra("image_name")){
            String imageUrl = getIntent().getStringExtra("image_url");
            String imageName = getIntent().getStringExtra("image_name");
            ImageUrl = imageUrl;
            setImage(imageUrl, imageName);
        }
    }

    //Permet de construire a l'aide de glide l'image a partir de l'url recu
    private void setImage(String imageUrl, String imageName){
        TextView name = findViewById(R.id.tv_lauch_puzzle);
        name.setText(imageName);

        ImageView image = findViewById(R.id.iv_lauch_puzzle);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }

    //Menu en haut a droite, permet de revenir a la liste des puzzles
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.laucher_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch (item.getItemId()){
           case R.id.back_main:
               Intent BackMain = new Intent(this, MainActivity.class);
               startActivity(BackMain);
           default:
               return super.onOptionsItemSelected(item);
       }
    }

    //Permet de commencer le puzzle
    public void launchPuzzle(View view){
        Intent gameActivity = new Intent(this, PuzzleGame.class);
        gameActivity.putExtra("ImagePuzzle",ImageUrl);
        gameActivity.putExtra("SeekBar",seekBar.getProgress());;
        startActivity(gameActivity);

    }
}

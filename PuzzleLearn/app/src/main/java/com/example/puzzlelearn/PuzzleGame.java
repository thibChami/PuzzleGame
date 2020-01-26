package com.example.puzzlelearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;


public class PuzzleGame extends AppCompatActivity {
    ArrayList<PuzzleP> pieces;          //on stock les pieces
    private Bitmap bitmap;                  //le bitmap associé a l image
    private ImageView imageView;            // image
    private int nbPieces = 3;                //nb de piece par default
    private int triche = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_game); // on recup le layout

        ImageView imageView = findViewById(R.id.imageView); //on recup l image
        String imageUrl = getIntent().getStringExtra("ImagePuzzle");// on recup l'url de l'image en cours
        nbPieces = getIntent().getIntExtra("SeekBar",3);// on recup le nimbre de piece
        new ConvertUrlToBitmap(imageView).execute(imageUrl);    //on converti l'url en image

    }

    // Fonction qui découpe l'image en fonction du nombre de pieces
    private ArrayList<PuzzleP> decoupeImage() {
        int nbPiece = nbPieces; // nb de piece
        int ligne = 3;              // nb de ligne
        int colonne = nbPiece/3;       //nb de colonne

        ImageView imageView = findViewById(R.id.imageView); //on recup l'image
        ArrayList<PuzzleP> pieces = new ArrayList<>(nbPiece); // on crée une liste de piece

        // on recup le le drawable de l'image puis le bitmap associé
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();


        int[] dimensions = getBitmapPos(imageView);//on recup la position du bitmap dans l'image
        int bitmapG = dimensions[0];    // on stock la position a gauche
        int bitmapH = dimensions[1];  // on stock la position en haut
        int bitmapLa = dimensions[2]; // on stock la largeur
        int bitmapLo= dimensions[3]; // on stock la longueur

        int ImageWidth = bitmapLa - 2 * abs(bitmapG); //on calcul la largeur de l'image recadré
        int ImageHeight = bitmapLo - 2 * abs(bitmapH);//on calcul la longueur de l'image recadré

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmapLa, bitmapLo, true); //on crée le bitmap redimentionné
        Bitmap newBitmap = Bitmap.createBitmap(scaledBitmap, abs(bitmapG), abs(bitmapH), ImageWidth, ImageHeight);//On crée le nv bitmap recadré

        // On calcule la largeur et la hauteur de la piece
        int pieceLa = ImageWidth/colonne;
        int pieceLo = ImageHeight/ligne;

        // Créez chaque pieces en  bitmap et on l'ajoute au tableau résultant
        int yCoord = 0;

        for (int i = 0; i < ligne; i++) {
            int xCoord = 0;


            for (int j = 0; j < colonne; j++) {
                Bitmap pieceBitmap = Bitmap.createBitmap(newBitmap, xCoord, yCoord, pieceLa, pieceLo);
                PuzzleP piece = new PuzzleP(getApplicationContext()); //on crée une nvl piece
                piece.setImageBitmap(pieceBitmap); //on lui set image bitmap
                piece.xCoord = xCoord; // on set la coordonée x
                piece.yCoord = yCoord;// on set la coordonée y
                piece.pieceWidth = pieceLa; // on set la laargeur
                piece.pieceHeight = pieceLo; // on set la longueur
                pieces.add(piece); // et on add la piece a la liste
                xCoord += pieceLa;// on augmente la coordonné x pour la prochaine piece

            }
            yCoord += pieceLo; // on augmente la coordonné x pour la prochaine piece
        }

        return pieces; // on retourne la liste des pieces
    }

    // methode qui renvoie la position du bitmap en fct de l'image
    private int[] getBitmapPos(ImageView imageView) {
        int[] res = new int[4]; //tableau  d'entierq qui contiendra les reusltats

        if (imageView == null || imageView.getDrawable() == null)
            return res; // si pas d'image ou si l'image ne contient rien

        // Get les dimensions de l'image
        // Get les valeurs de la matrice de l'image et on les places dan sun tableaux
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

       //On extrait les valeurs d'échelle en utilisant les constantes de la matrice
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // On récupère le dessinable , le drawable  de l'image
        final Drawable d = imageView.getDrawable();
        final int orginLa = d.getIntrinsicWidth(); // get largeur
        final int orginLo = d.getIntrinsicHeight();// get longueur

        // calcul de la dimension actuelle
        final int actLa = Math.round(orginLa * scaleX);
        final int actLo = Math.round(orginLo * scaleY);

        //stock les resultats
        res[2] = actLa;
        res[3] = actLo;

        // Get la position de l'image
        int imageLa = imageView.getWidth();
        int imgLo = imageView.getHeight();

        int haut  = (int) (imgLo - actLo)/2;
        int gauche = (int) (imageLa - actLa)/2;

        //stock les resultats
        res[0] = gauche;
        res[1] = haut;

        return res;
    }



    //Fonction pour bouger les pieces dans l'images
    public class TouchListener implements View.OnTouchListener {
        private float xDelta;
        private float yDelta;

        @Override   //fonction quant on appuie sur la piece
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float x = motionEvent.getRawX(); // on recup le x quant on bouge la piece
            float y = motionEvent.getRawY();// on recup le y quant on bouge la piece
            final RelativeLayout layout = findViewById(R.id.layout);

            //marge d'erreur pour le placement de l'image
            final double tolerance = sqrt(pow(view.getWidth(), 2) + pow(view.getHeight(), 2)) / 10;

            //On recupère la piece
            PuzzleP piece = (PuzzleP) view;
            if (!piece.canMove) {//test si elle bouge
                return true;
            }

            //recup le layout
            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    xDelta = x - lParams.leftMargin;//bouge quant on appuie


                    yDelta = y - lParams.topMargin;//bouge quant on apppuie
                    piece.bringToFront();
                    break;
                //action quant on bouge la piece
                case MotionEvent.ACTION_MOVE:
                    if (! ((x - xDelta) > layout.getWidth() - piece.pieceWidth) && (x - xDelta )> 0 ){
                        lParams.leftMargin = (int) (x - xDelta); // bouge la piece suivant x quant on bouge le doit
                    }
                    if (! ((y - yDelta) > layout.getHeight() - piece.pieceHeight) && (y - yDelta) > 01){
                        lParams.topMargin = (int) (y - yDelta);// bouge la piece suivant y quant on bouge le doit
                    }

                       view.setLayoutParams(lParams); // set les parametres qui ont changées au layout
                    break;

                //action quant on leve le doit de la piece , pour placer la piece
                case MotionEvent.ACTION_UP:
                    int xDiff = abs(piece.xCoord - lParams.leftMargin); // on tes la diff entre la position de la piece et la position x
                    int yDiff = abs(piece.yCoord - lParams.topMargin);// on tes la diff entre la position de la piece et la position x
                    if (xDiff <= tolerance && yDiff <= tolerance) {// si la piece est au bonne endroit
                        lParams.leftMargin = piece.xCoord;  //on met la piece au bonne en droit  -- x
                        lParams.topMargin = piece.yCoord;//                                      ---- Y
                        piece.setLayoutParams(lParams); // on set les parametres
                        piece.canMove = false;// on set le canMove a faux la piece peu pas bouger
                        sendViewToBack(piece); // on set la piece a la vu
                    }
                    break;
            }
            //test a chaque foi qu'on bouge une piece
            if (testFin(pieces)){
                fin();
            }
            return true;
        }

        public void sendViewToBack(final View child) {
            final ViewGroup parent = (ViewGroup)child.getParent();
            if (null != parent) {
                parent.removeView(child);
                parent.addView(child, 0);
            }
        }
    }

    private void fin() {
        //creation d'un pop up de fin

        AlertDialog.Builder builder = new AlertDialog.Builder(this); //creation del'alert builder
        builder.setCancelable(true);
        builder.setTitle("Victoire");
        builder.setMessage("Bravo c'est gagné");
        builder.setPositiveButton("Retour",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent BackMain = new Intent(PuzzleGame.this,MainActivity.class);//retour a la classe des menus
                        startActivity(BackMain);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // methode qui test si on a bien placé toutes les pieces
    private boolean testFin(ArrayList<PuzzleP> pieces) {
        boolean fin = true;
        for(PuzzleP  piece : pieces) {
            fin = fin && !piece.canMove; // boolean qui test si les pieces peuvent bougées
        }
        return fin;
    }

    //class pour le téléchargement de l'image
    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Boolean> {
        ImageView image; // mon image
        //methode pour convertir une url en bitmap
        public ConvertUrlToBitmap(ImageView imageView) {
            this.image = imageView;
        }

        //methode utilisé en fond
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]); // on recup l'url
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream()); //on decode l'url et on la trnasform en bitmap
                return true;
            } catch (Exception e) {
                Log.e("e", e.toString());
                return false;
            }
        }
        //methode call après le telechargement d'image

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean) {
                //creation d'un thread apres avoir télécharger l'image
                PuzzleGame.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(bitmap);//on set le bitmap a l'image
                        final RelativeLayout layout = findViewById(R.id.layout);//on recup le layout
                        ImageView imageView = findViewById(R.id.imageView);//on recup l'image

                        //ajout des pieces au layout
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                pieces = decoupeImage(); // on stock les pieces découpées dans la liste
                                TouchListener touchListener = new TouchListener(); //on créé un nv touchlistner pour pouvoir bouger les pieces
                                Collections.shuffle(pieces);//on melange les pieces
                                for(PuzzleP  piece : pieces) {// parcours de la liste de piece
                                    piece.setOnTouchListener(touchListener);//on set le touch listner a chaque piece
                                    layout.addView(piece);//on a ajoute chaque piece a la vue

                                }
                            }
                        });

                    }
                });
            } else {
                //telechagement error
                Log.e("error ", "image dowlaod failled")       ;     }
        }
    }

    //classe Puzzle  qui stocke la coordonnée x et y,
    //  la largeur et la longueur de la piece
    // si la piece peut bouger
    public class PuzzleP extends AppCompatImageView {
        public int xCoord;
        public int yCoord;
        public int pieceWidth;
        public int pieceHeight;
        public boolean canMove = true;

        public PuzzleP(Context context) {
            super(context);
        }
    }

    private void triche() {
        //creation d'un pop up de fin

        Dialog builder = new Dialog(this);
        builder.setCancelable(true);
        builder.setTitle("Victoire");

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);//on set le bitmap a l'image

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        builder.show();
    }

    private void credit () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Credit");
        builder.setMessage("Vous n'avez plus de crédit de triche Veuillez en acheter !");

        builder.setPositiveButton("Payer !!!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.triche:
                if (triche < 2){
                    triche ++;

                    triche();

                }else{
                    credit();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
package com.example.puzzlelearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

//Permet la gestion de la bdd
public class MySQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Puzzle_Manager";
    private static final String TABLE_PUZZLE = "Puzzle";
    private static final String COLUMN_PUZZLE_ID ="Puzzle_Id";
    private static final String COLUMN_PUZZLE_TITLE ="Puzzle_Title";
    private static final String COLUMN_PUZZLE_URL = "Puzzle_Url";

    public MySQLite(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creation de la table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_PUZZLE + "("
                + COLUMN_PUZZLE_ID + " INTEGER PRIMARY KEY," + COLUMN_PUZZLE_TITLE + " TEXT,"
                + COLUMN_PUZZLE_URL + " TEXT" + ")";
        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUZZLE);
        onCreate(db);
    }
    //Ajout d'un puzzle dans la table Puzzle
    public void addPuzzle(Puzzle puzzle) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PUZZLE_TITLE, puzzle.getNom_puzzle());
        values.put(COLUMN_PUZZLE_URL, puzzle.getUrl_puzzle());
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        db.insert(TABLE_PUZZLE, null, values);

    }
    //met dans une arrayList tous les puzzles de la table
    public ArrayList<Puzzle> getAllPuzzles() {
        String selectQuery = "SELECT  * FROM " + TABLE_PUZZLE;
        ArrayList<Puzzle> puzzleList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String url = cursor.getString(2);
                puzzleList.add(new Puzzle(id, name, url));
            } while (cursor.moveToNext());
        }


        return puzzleList;
    }

    //Permet de modifier un puzzle(Pas utlisé encore)
    public int updatePuzzle(Puzzle puzzle) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PUZZLE_TITLE, puzzle.getNom_puzzle());
        values.put(COLUMN_PUZZLE_URL, puzzle.getUrl_puzzle());
        SQLiteDatabase db = this.getWritableDatabase();
        // updating row
        return db.update(TABLE_PUZZLE, values, COLUMN_PUZZLE_ID + " = ?",
                new String[]{String.valueOf(puzzle.getId_puzzle())});
    }


    //Permet de supprimer un puzzle de la table
    public void deletePuzzle(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUZZLE, COLUMN_PUZZLE_ID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();
    }

}
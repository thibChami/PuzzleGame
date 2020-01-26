package com.example.puzzlelearn;

public class Puzzle {
    private int id_puzzle;
    private String nom_puzzle;
    private String url_puzzle;



    // Constructeur
    public Puzzle(String nom, String url) {
        this.nom_puzzle = nom;
        this.url_puzzle = url;
    }

    public Puzzle(int id, String nom, String url) {
        this.id_puzzle=id;
        this.nom_puzzle=nom;
        this.url_puzzle=url;
    }

    //Methode
    public int getId_puzzle() {
        return id_puzzle;
    }

    public void setId_puzzle(int id) {
        this.id_puzzle = id;
    }

    public String getNom_puzzle() {
        return nom_puzzle;
    }

    public void setNom_puzzle(String nom) {
        this.nom_puzzle = nom;
    }

    public String getUrl_puzzle() {
        return url_puzzle;
    }

    public void setUrl_puzzle(String url) {
        this.url_puzzle = url;
    }



}

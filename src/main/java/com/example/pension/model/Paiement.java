package com.example.pension.model;

import java.time.LocalDate;

public class Paiement {

    private int id;
    private String im;
    private String numTarif;
    private LocalDate date;

    // Association avec les 2 autres modèles
    private Personne personne;
    private Tarif tarif;

    public Paiement() {
    }

    public Paiement(String im, String numTarif, LocalDate date) {
        this.im = im;
        this.numTarif = numTarif;
        this.date = date;
    }

    // Getters & Setters des objets liés
    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }

    public Tarif getTarif() { return tarif; }
    public void setTarif(Tarif tarif) { this.tarif = tarif; }

    // Getters pratiques pour les TableColumn JavaFX
    public String getNom() { return personne != null ? personne.getNom() : ""; }
    public String getPrenom() { return personne != null ? personne.getPrenoms() : ""; }
    public String getDiplome() { return personne != null ? personne.getDiplome() : ""; }
    public Integer getMontant() { return tarif != null ? tarif.getMontant() : 0; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIm() { return im; }
    public void setIm(String im) { this.im = im; }

    public String getNumTarif() { return numTarif; }
    public void setNumTarif(String numTarif) { this.numTarif = numTarif; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
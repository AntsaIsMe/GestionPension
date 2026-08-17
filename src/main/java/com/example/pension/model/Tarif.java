package com.example.pension.model;

public class Tarif {

    private String numTarif;
    private String diplome;
    private String categorie;
    private int montant;

    public Tarif() {
    }

    public Tarif(String numTarif, String diplome, String categorie, int montant) {
        this.numTarif = numTarif;
        this.diplome = diplome;
        this.categorie = categorie;
        this.montant = montant;
    }

    public String getNumTarif() {
        return numTarif;
    }

    public void setNumTarif(String numTarif) {
        this.numTarif = numTarif;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    @Override
    public String toString() {
        return numTarif + " - " + diplome + " (" + categorie + ") : " + montant + " Ar";
    }
}

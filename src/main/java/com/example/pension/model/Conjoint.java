package com.example.pension.model;

public class Conjoint {

    private int id;
    private String numPension;   // IM de la personne décédée
    private String nomConjoint;
    private String prenomConjoint;
    private int montant;         // 40% du montant de la pension du défunt

    public Conjoint() {
    }

    public Conjoint(String numPension, String nomConjoint, String prenomConjoint, int montant) {
        this.numPension = numPension;
        this.nomConjoint = nomConjoint;
        this.prenomConjoint = prenomConjoint;
        this.montant = montant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumPension() {
        return numPension;
    }

    public void setNumPension(String numPension) {
        this.numPension = numPension;
    }

    public String getNomConjoint() {
        return nomConjoint;
    }

    public void setNomConjoint(String nomConjoint) {
        this.nomConjoint = nomConjoint;
    }

    public String getPrenomConjoint() {
        return prenomConjoint;
    }

    public void setPrenomConjoint(String prenomConjoint) {
        this.prenomConjoint = prenomConjoint;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }
}

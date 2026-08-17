package com.example.pension.model;

import java.time.LocalDate;

public class Personne {

    private String im;
    private String nom;
    private String prenoms;
    private LocalDate dateNais;
    private String diplome;
    private String contact;
    private boolean statut;      // true = vivant, false = décédé
    private String situation;    // divorcé(e), marié(e), veuf(ve)
    private String nomConjoint;
    private String prenomConjoint;

    public Personne() {
    }

    public Personne(String im, String nom, String prenoms, LocalDate dateNais, String diplome,
                     String contact, boolean statut, String situation,
                     String nomConjoint, String prenomConjoint) {
        this.im = im;
        this.nom = nom;
        this.prenoms = prenoms;
        this.dateNais = dateNais;
        this.diplome = diplome;
        this.contact = contact;
        this.statut = statut;
        this.situation = situation;
        this.nomConjoint = nomConjoint;
        this.prenomConjoint = prenomConjoint;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public LocalDate getDateNais() {
        return dateNais;
    }

    public void setDateNais(LocalDate dateNais) {
        this.dateNais = dateNais;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
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

    @Override
    public String toString() {
        return im + " - " + nom + " " + prenoms;
    }
}

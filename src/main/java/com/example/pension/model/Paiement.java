package com.example.pension.model;

import java.time.LocalDate;

public class Paiement {

    private int id;
    private String im;
    private String numTarif;
    private LocalDate date;

    public Paiement() {
    }

    public Paiement(String im, String numTarif, LocalDate date) {
        this.im = im;
        this.numTarif = numTarif;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getNumTarif() {
        return numTarif;
    }

    public void setNumTarif(String numTarif) {
        this.numTarif = numTarif;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

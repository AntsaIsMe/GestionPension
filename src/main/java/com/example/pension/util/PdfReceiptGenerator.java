package com.example.pension.util;

import com.example.pension.model.Paiement;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.TextStyle;
import java.util.Locale;

public class PdfReceiptGenerator {

    static {
        // Pré-initialisation de Commons Logging pour éviter le NullPointerException
        try {
            LogFactory.getLog("org.apache.pdfbox");
        } catch (Throwable ignored) {}
    }

    public void genererRecu(Personne personne, Tarif tarif, Paiement paiement, String cheminSortie) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A5);
            document.addPage(page);

            String mois = paiement.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            String moisCapitalise = mois.substring(0, 1).toUpperCase() + mois.substring(1);
            // Nettoie les caractères d'espacement Unicode non supportés par Helvetica (ex: U+202F, U+00A0)
            String montantFormate = NumberFormat.getInstance(Locale.FRANCE)
                    .format(tarif.getMontant())
                    .replace('\u202F', ' ')
                    .replace('\u00A0', ' ');

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float margeGauche = 60;
                float y = 550;
                float interligne = 22;

                // En-tête
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                cs.beginText();
                cs.newLineAtOffset(margeGauche, y);
                cs.showText("RECU DE PAIEMENT DE PENSION");
                cs.endText();
                y -= interligne * 2;

                // Contenu
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                String[] lignes = {
                        "IM : " + (personne.getIm() != null ? personne.getIm() : ""),
                        "Nom : " + (personne.getNom() != null ? personne.getNom() : ""),
                        "Prenoms : " + (personne.getPrenoms() != null ? personne.getPrenoms() : ""),
                        "Mois : " + moisCapitalise,
                        "Annee : " + paiement.getDate().getYear(),
                        "Montant : " + montantFormate + " Ar"
                };

                for (String ligne : lignes) {
                    cs.beginText();
                    cs.newLineAtOffset(margeGauche, y);
                    cs.showText(ligne);
                    cs.endText();
                    y -= interligne;
                }
            }

            document.save(cheminSortie);
        }
    }
}
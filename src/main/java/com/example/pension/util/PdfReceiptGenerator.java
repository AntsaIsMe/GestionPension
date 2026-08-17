package com.example.pension.util;

import com.example.pension.model.Paiement;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Génère un reçu de paiement de pension au format PDF,
 * sur le modèle donné dans l'énoncé du projet.
 */
public class PdfReceiptGenerator {

    public void genererRecu(Personne personne, Tarif tarif, Paiement paiement, String cheminSortie) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            String mois = paiement.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            String moisCapitalise = mois.substring(0, 1).toUpperCase() + mois.substring(1);
            String montantFormate = NumberFormat.getInstance(Locale.FRANCE).format(tarif.getMontant());

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float margeGauche = 60;
                float y = 750;
                float interligne = 22;

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                cs.beginText();
                cs.newLineAtOffset(margeGauche, y);
                cs.showText("RECU DE PAIEMENT DE PENSION");
                cs.endText();
                y -= interligne * 2;

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                String[] lignes = {
                        "IM : " + personne.getIm(),
                        "Nom : " + personne.getNom(),
                        "Prenoms : " + personne.getPrenoms(),
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

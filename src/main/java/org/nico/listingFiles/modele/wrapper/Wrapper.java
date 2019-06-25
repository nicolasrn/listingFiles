package org.nico.listingFiles.modele.wrapper;

import org.nico.listingFiles.modele.DescriptionFichier;

import java.util.List;

public abstract class Wrapper {

    protected int occurences;
    protected List<DescriptionFichier> fichiers;

    public Wrapper(List<DescriptionFichier> fichiers) {
        this.fichiers = fichiers;
        this.occurences = fichiers.size();
        computeDoublons();
    }

    public int getOccurences() {
        return occurences;
    }

    public List<DescriptionFichier> getFichiers() {
        return fichiers;
    }

    protected void computeDoublons() {
        int size = fichiers.size();
        if (size == 1) {
            return;
        }
        for (int i = 0; i < size - 1; i++) {
            if (isChecksumIdentique(fichiers.get(i), fichiers.get(i + 1))) {
                marquerCommeDoublon(fichiers.get(i), fichiers.get(i + 1));
            }
        }
        if (isChecksumIdentique(fichiers.get(size - 1), fichiers.get(size - 2))) {
            marquerCommeDoublon(fichiers.get(size - 1), fichiers.get(size - 2));
        }
    }

    protected void marquerCommeDoublon(DescriptionFichier descriptionFichier, DescriptionFichier descriptionFichier1) {
        descriptionFichier.setDoublon(true);
        descriptionFichier1.setDoublon(true);
    }

    protected boolean isChecksumIdentique(DescriptionFichier descriptionFichier, DescriptionFichier descriptionFichier1) {
        return descriptionFichier.getChecksum().equals(descriptionFichier1.getChecksum());
    }
}

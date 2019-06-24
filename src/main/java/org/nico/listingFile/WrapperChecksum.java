package org.nico.listingFile;

import org.nico.listingFile.modele.DescriptionFichier;

import java.util.List;

public class WrapperChecksum {
    private final int occurences;
    private final List<DescriptionFichier> descriptionFichiers;

    public WrapperChecksum(List<DescriptionFichier> descriptionFichiers) {
        this.descriptionFichiers = descriptionFichiers;
        this.occurences = descriptionFichiers.size();
    }

    public int getOccurences() {
        return occurences;
    }
}

package org.nico.listingFile.modele.filtre;

import org.nico.listingFile.WrapperChecksum;
import org.nico.listingFile.modele.WrapperDescriptionFichiers;

import java.util.AbstractMap;
import java.util.function.Predicate;

public enum FiltreChecksum {
    DEFAUT(entry -> true),
    TAILLE(entry -> entry.getValue().getOccurences() > 1);

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksum>> filter;

    FiltreChecksum(Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksum>> filter) {
        this.filter = filter;
    }

    public Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksum>> getFiltre() {
        return filter;
    }
}

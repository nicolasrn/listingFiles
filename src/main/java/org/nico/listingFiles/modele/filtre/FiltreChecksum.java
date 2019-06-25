package org.nico.listingFiles.modele.filtre;

import org.nico.listingFiles.modele.wrapper.WrapperChecksums;

import java.util.AbstractMap;
import java.util.function.Predicate;

public enum FiltreChecksum {
    DEFAUT(entry -> true),
    TAILLE(entry -> entry.getValue().getOccurences() > 1);

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksums>> filter;

    FiltreChecksum(Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksums>> filter) {
        this.filter = filter;
    }

    public Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksums>> getFiltre() {
        return filter;
    }
}

package org.nico.listingFile.modele.filtre;

import org.nico.listingFile.modele.WrapperDescriptionFichiers;

import java.util.AbstractMap;
import java.util.function.Predicate;

public enum Filtre {
    DEFAUT(entry -> true),
    TAILLE(entry -> entry.getValue().getOccurences() > 1);

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> filter;

    Filtre(Predicate<? super AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> filter) {
        this.filter = filter;
    }

    public Predicate<? super AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> getFiltre() {
        return filter;
    }
}

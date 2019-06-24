package org.nico.listingFile.modele.filtre;

import org.nico.listingFile.modele.wrapper.WrapperParNomFichier;

import java.util.AbstractMap;
import java.util.function.Predicate;

public enum FiltreParNom {
    DEFAUT(entry -> true),
    TAILLE(entry -> entry.getValue().getOccurences() > 1);

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperParNomFichier>> filter;

    FiltreParNom(Predicate<? super AbstractMap.SimpleEntry<String, WrapperParNomFichier>> filter) {
        this.filter = filter;
    }

    public Predicate<? super AbstractMap.SimpleEntry<String, WrapperParNomFichier>> getFiltre() {
        return filter;
    }
}

package org.nico.listingFiles.algo;

import org.nico.listingFiles.modele.DescriptionFichier;
import org.nico.listingFiles.modele.filtre.FiltreParNom;
import org.nico.listingFiles.modele.wrapper.WrapperParNomFichier;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RegroupementParNomFichier implements IAlgo<WrapperParNomFichier> {

    private FiltreParNom filtreParNom;

    @Override
    public Map<String, WrapperParNomFichier> getRegroupement(List<File> files) {
        return files.stream()
                .collect(Collectors.groupingBy(File::getName, Collectors.mapping(this::descriptionFichier, Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), wrapperDescriptionFichiers(entry.getValue())))
                .filter(getFilter())
                .sorted(getComparatorFileWrapper())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    public void setFiltreParNom(String filtreParNom) {
        this.filtreParNom = FiltreParNom.valueOf(filtreParNom);
    }

    private DescriptionFichier descriptionFichier(File file) {
        return new DescriptionFichier(file);
    }

    private WrapperParNomFichier wrapperDescriptionFichiers(List<DescriptionFichier> descriptionFichiers) {
        return new WrapperParNomFichier(descriptionFichiers);
    }

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperParNomFichier>> getFilter() {
        if (filtreParNom == null) {
            return FiltreParNom.DEFAUT.getFiltre();
        }
        return filtreParNom.getFiltre();
    }

    private Comparator<AbstractMap.SimpleEntry<String, WrapperParNomFichier>> getComparatorFileWrapper() {
        Comparator<AbstractMap.SimpleEntry<String, WrapperParNomFichier>> comparing = Comparator.comparing(a -> a.getValue().getOccurences());
        comparing = comparing.thenComparing(Map.Entry.comparingByKey()).reversed();
        return comparing;
    }
}

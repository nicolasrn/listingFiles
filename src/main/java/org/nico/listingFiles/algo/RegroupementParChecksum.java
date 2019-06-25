package org.nico.listingFiles.algo;

import org.nico.listingFiles.modele.DescriptionFichier;
import org.nico.listingFiles.modele.filtre.FiltreChecksum;
import org.nico.listingFiles.modele.wrapper.WrapperChecksums;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RegroupementParChecksum implements IAlgo<WrapperChecksums> {

    private FiltreChecksum filtre;

    @Override
    public Map<String, WrapperChecksums> getRegroupement(List<File> files) {
        return files.stream()
                .map(this::descriptionFichier)
                .collect(Collectors.groupingBy(DescriptionFichier::getChecksum, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), wrapperChecksum(entry.getValue())))
                .filter(getFilterChecksum())
                .sorted(getComparatorChecksumFileWrapper())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    public void setFiltreParNom(String filtreParNom) {
        this.filtre = FiltreChecksum.valueOf(filtreParNom);
    }

    private DescriptionFichier descriptionFichier(File file) {
        return new DescriptionFichier(file);
    }

    private WrapperChecksums wrapperChecksum(List<DescriptionFichier> descriptionFichiers) {
        return new WrapperChecksums(descriptionFichiers);
    }

    private Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksums>> getFilterChecksum() {
        if (filtre == null) {
            return FiltreChecksum.DEFAUT.getFiltre();
        }
        return filtre.getFiltre();
    }

    private Comparator<? super AbstractMap.SimpleEntry<String, WrapperChecksums>> getComparatorChecksumFileWrapper() {
        Comparator<AbstractMap.SimpleEntry<String, WrapperChecksums>> comparing = Comparator.comparing(a -> a.getValue().getOccurences());
        comparing = comparing.thenComparing(Map.Entry.comparingByKey()).reversed();
        return comparing;
    }
}

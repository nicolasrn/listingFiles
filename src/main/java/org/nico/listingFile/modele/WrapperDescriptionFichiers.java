package org.nico.listingFile.modele;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WrapperDescriptionFichiers {
    private final int occurences;
    private final List<DescriptionFichier> fichiers;
    private final Map<String, List<DescriptionFichier>> checksumIdentiques;

    public WrapperDescriptionFichiers(List<DescriptionFichier> wrappers) {
        fichiers = wrappers;
        occurences = wrappers.size();
        checksumIdentiques = wrappers.stream()
                .collect(Collectors.groupingBy(DescriptionFichier::getChecksum))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        computeDoublons();
    }

    public int getOccurences() {
        return occurences;
    }

    public List<DescriptionFichier> getFichiers() {
        return fichiers;
    }

    private void computeDoublons() {
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

    private void marquerCommeDoublon(DescriptionFichier descriptionFichier, DescriptionFichier descriptionFichier1) {
        descriptionFichier.setDoublon(true);
        descriptionFichier1.setDoublon(true);
    }

    private boolean isChecksumIdentique(DescriptionFichier descriptionFichier, DescriptionFichier descriptionFichier1) {
        return descriptionFichier.getChecksum().equals(descriptionFichier1.getChecksum());
    }
}

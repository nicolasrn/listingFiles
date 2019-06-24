package org.nico.listingFile.algo;

import org.nico.listingFile.modele.wrapper.Wrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IAlgo<T extends Wrapper> {

    Map<String, T> getRegroupement(List<File> files);

    void setFiltreParNom(String filtreParNom);
}

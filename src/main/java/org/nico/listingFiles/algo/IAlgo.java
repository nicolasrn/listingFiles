package org.nico.listingFiles.algo;

import org.nico.listingFiles.modele.wrapper.Wrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IAlgo<T extends Wrapper> {

    Map<String, T> getRegroupement(List<File> files);

    void setFiltreParNom(String filtreParNom);
}

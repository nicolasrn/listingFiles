package org.nico.listingFiles.algo;

import org.nico.listingFiles.modele.wrapper.Wrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public enum EAlgo {
    CHECKSUM(new RegroupementParChecksum()),
    NOMFICHIER(new RegroupementParNomFichier()),
    DEFAUT(new RegroupementParChecksum());

    private IAlgo algo;

    EAlgo(IAlgo algo) {
        this.algo = algo;
    }

    public Map<String, Wrapper> execute(List<File> files, String filtre) {
        algo.setFiltreParNom(filtre);
        return algo.getRegroupement(files);
    }
}

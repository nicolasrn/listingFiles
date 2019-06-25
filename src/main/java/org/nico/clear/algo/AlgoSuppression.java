package org.nico.clear.algo;

import java.io.File;
import java.util.stream.Stream;

public enum AlgoSuppression {
    CHECKSUM(new SuppressionRedondanceChecksum()),
    NOMFICHIER(new SuppressionRedondanceNomFichier());

    private final AbstractSuppressionRedondance suppressionRedondance;

    AlgoSuppression(AbstractSuppressionRedondance suppressionRedondance) {
        this.suppressionRedondance = suppressionRedondance;
    }

    public static AlgoSuppression find(String name) {
        return Stream.of(values())
                .filter(algo -> name.toLowerCase().contains(algo.name().toLowerCase()))
                .findFirst()
                .get();
    }

    public void execute(File file) {
        suppressionRedondance.analyserEtTraiter(file);
    }
}

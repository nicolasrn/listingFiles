package org.nico.suppressionsredondances.algo;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public enum AlgoSuppression {
    CHECKSUM(SuppressionRedondanceChecksum::new),
    NOMFICHIER(SuppressionRedondanceNomFichier::new);

    private final Function<String, AbstractSuppressionRedondance> initialiseurAlgorithme;

    AlgoSuppression(Function<String, AbstractSuppressionRedondance> initialiseurAlgorithme) {
        this.initialiseurAlgorithme = initialiseurAlgorithme;
    }

    public static AlgoSuppression find(String name) {
        return Stream.of(values())
                .filter(algo -> name.toLowerCase().contains(algo.name().toLowerCase()))
                .findFirst()
                .get();
    }

    public void execute(File file, String destination) {
        initialiseurAlgorithme.apply(destination).analyserEtTraiter(file);
    }
}

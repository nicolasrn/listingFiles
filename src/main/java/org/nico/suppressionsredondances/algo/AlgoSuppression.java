package org.nico.suppressionsredondances.algo;

import java.io.File;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public enum AlgoSuppression {
    CHECKSUM(SuppressionRedondanceChecksum::new),
    NOMFICHIER(SuppressionRedondanceNomFichier::new);

    private final BiFunction<String, Boolean, AbstractSuppressionRedondance> initialiseurAlgorithme;

    AlgoSuppression(BiFunction<String, Boolean, AbstractSuppressionRedondance> initialiseurAlgorithme) {
        this.initialiseurAlgorithme = initialiseurAlgorithme;
    }

    public static AlgoSuppression find(String name) {
        return Stream.of(values())
                .filter(algo -> name.toLowerCase().contains(algo.name().toLowerCase()))
                .findFirst()
                .get();
    }

    public void execute(File file, String destination, boolean soft) {
        initialiseurAlgorithme.apply(destination, soft).analyserEtTraiter(file);
    }
}

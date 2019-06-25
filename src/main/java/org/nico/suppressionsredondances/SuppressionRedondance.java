package org.nico.suppressionsredondances;

import org.apache.commons.cli.*;
import org.nico.suppressionsredondances.algo.AlgoSuppression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.stream.Stream;

public class SuppressionRedondance {
    private static final Logger LOG = LoggerFactory.getLogger(SuppressionRedondance.class);
    private static String[] FILES = null;

    public static void main(String[] args) {
        try {
            initCommandLine(args);
            Stream.of(FILES).map(file -> new File(file)).forEach(SuppressionRedondance::traiter);
        } catch (Exception e) {
            LOG.error("erreur inattendue", e);
        }
    }

    private static void traiter(File file) {
        AlgoSuppression.find(file.getName()).execute(file);
    }

    private static void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(getOptions(), args);
        FILES = cmd.getOptionValues("f");
    }

    private static Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option("f", true, "les fichiers à traiter"));
        return opts;
    }
}

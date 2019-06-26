package org.nico.suppressionsredondances;

import org.apache.commons.cli.*;
import org.nico.ITypeApp;
import org.nico.chrono.Chrono;
import org.nico.suppressionsredondances.algo.AlgoSuppression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.stream.Stream;

public class SuppressionRedondance implements ITypeApp {
    private static final Logger LOG = LoggerFactory.getLogger(SuppressionRedondance.class);
    private String[] FILES = null;
    private String DESTINATION = null;

    @Override
    public void main(String[] args) {
        try {
            initCommandLine(args);
            Chrono.getInstance().start("traitement de suppression / backup");
            Stream.of(FILES).map(File::new).forEach(this::traiter);
            Chrono.getInstance().end();
        } catch (Exception e) {
            LOG.error("erreur inattendue", e);
        }
    }

    private void traiter(File file) {
        AlgoSuppression.find(file.getName()).execute(file, DESTINATION);
    }

    private void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(getOptions(), args);
        FILES = cmd.getOptionValues("f");
        DESTINATION = cmd.getOptionValue("d", "./backup");
    }

    private Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option("f", true, "le(s) fichier(s) à traiter"));
        opts.addOption(new Option("d", true, "le répertoire où faire la sauvegarde (par défaut ./backup"));
        return opts;
    }
}

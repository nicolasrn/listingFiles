package org.nico.suppressionsredondances;

import org.apache.commons.cli.*;
import org.nico.App;
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
    private boolean SOFT = true;

    @Override
    public void main(String[] args) {
        try {
            initCommandLine(args);
            execute(FILES, DESTINATION, SOFT);
        } catch (Exception e) {
            LOG.error("erreur inattendue", e);
        }
    }

    public void execute(String[] files, String destination, boolean soft) {
        try {
            Chrono.getInstance().start("traitement de suppression / backup");
            Stream.of(files).map(File::new).forEach(file -> traiter(file, destination, soft));
            Chrono.getInstance().end();
        } catch (Exception e) {
            LOG.error("erreur inattendue", e);
        }
    }

    private void traiter(File file, String destination, boolean soft) {
        AlgoSuppression.find(file.getName()).execute(file, destination, soft);
    }

    private void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(getOptions(), args, true);
        if (cmd.hasOption("h")) {
            App.afficherAide("-" + App.Type.SUPPRESSION.name(), this::getOptions);
            System.exit(0);
        }
        FILES = cmd.getOptionValues("f");
        DESTINATION = cmd.getOptionValue("d", "./backup");
        SOFT = Boolean.parseBoolean(cmd.getOptionValue("s", "true"));
    }

    private Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option(App.Type.SUPPRESSION.name(), false, "l'algo courant"));
        opts.addOption(new Option("f", "file", true, "le(s) fichier(s) à traiter"));
        opts.addOption(new Option("d", "destination", true, "le répertoire où faire la sauvegarde (par défaut ./backup"));
        opts.addOption(new Option("s", "soft", true, "prend pour valeur true ou false, indique si un backup doit être effectué (par défaut true)"));
        opts.addOption(new Option("h", "help", false, "affiche ce message d'aide"));
        return opts;
    }
}

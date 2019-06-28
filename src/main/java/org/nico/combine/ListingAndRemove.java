package org.nico.combine;

import org.apache.commons.cli.*;
import org.nico.App;
import org.nico.ITypeApp;
import org.nico.listingFiles.ListingFiles;
import org.nico.listingFiles.algo.EAlgo;
import org.nico.listingFiles.modele.filtre.FiltreChecksum;
import org.nico.suppressionsredondances.SuppressionRedondance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ListingAndRemove implements ITypeApp {

    private static final Logger LOG = LoggerFactory.getLogger(ListingAndRemove.class);
    private static String DESTINATION = null;

    @Override
    public void main(String[] args) {
        try {
            initCommandLine(args);
            execute(DESTINATION);
        } catch (Exception e) {
            LOG.error("erreur inattendue", e);
        }
    }

    private void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(getOptions(), args, true);
        if (cmd.hasOption("h")) {
            App.afficherAide("-" + App.Type.SUPPRESSION.name(), this::getOptions);
            System.exit(0);
        }
        DESTINATION = cmd.getOptionValue("d");
    }

    private Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option(App.Type.LISTING_AND_REMOVE.name(), false, "l'algo courant"));
        opts.addOption(new Option("d", "destination", true, "le répertoire où faire la sauvegarde (par défaut ./backup"));
        return opts;
    }

    private void execute(String destination) {
        ListingFiles listingFiles = new ListingFiles();
        listingFiles.execute(EAlgo.CHECKSUM.name(), ".", FiltreChecksum.TAILLE.name());

        SuppressionRedondance suppressionRedondance = new SuppressionRedondance();
        suppressionRedondance.execute(listingFiles.getFichiersGeneres().stream().map(File::getPath).toArray(String[]::new), destination, true);
    }
}

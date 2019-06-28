package org.nico;

import org.apache.commons.cli.*;
import org.nico.combine.ListingAndRemove;
import org.nico.listingFiles.ListingFiles;
import org.nico.suppressionsredondances.SuppressionRedondance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static String TYPE;

    public static void main(String args[]) {
        try {
            args = initCommandLine(args);
            Type.valueOf(TYPE).execute(args);
        } catch (Exception e) {
            LOG.error("erreur inatendue", e);
        }
    }

    private static String[] initCommandLine(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(getOptions(), args, true);
            if (args[0].equals("-h")) {
                afficherAide(null, App::getOptions);
                System.exit(0);
            }
            TYPE = Stream.of(Type.values())
                    .map(Type::name)
                    .filter(app -> cmd.hasOption("-" + app))
                    .findFirst()
                    .orElseThrow(() -> new Exception("type application non trouvé"));
            if (!TYPE.isEmpty()) {
                args = Stream.of(args).filter(arg -> !arg.equals(TYPE)).toArray(String[]::new);
            }
            return args;
        } catch (ParseException e) {
            afficherAide(null, App::getOptions);
        } catch (Exception e) {
            LOG.error("exception non attendue", e);
        }
        return args;
    }

    public static void afficherAide(String lineSyntaxe, Supplier<Options> optionsSupplier) {
        if (lineSyntaxe == null) {
            lineSyntaxe = "";
        }
        new HelpFormatter().printHelp("occurences" + (lineSyntaxe.isEmpty() ? "" : " " + lineSyntaxe), optionsSupplier.get(), true);
    }

    private static Options getOptions() {
        Options options = new Options();
        Stream.of(Type.values()).map(Type::name).map(nom -> new Option(nom, false, "execute le programme " + nom)).forEach(options::addOption);
        options.addOption("h", "help", false, "affiche cette aide");
        return options;
    }

    public enum Type {
        LISTING(ListingFiles::new),
        SUPPRESSION(SuppressionRedondance::new),
        LISTING_AND_REMOVE(ListingAndRemove::new);

        private Supplier<ITypeApp> initialisateurApp;

        Type(Supplier<ITypeApp> initialisateurApp) {
            this.initialisateurApp = initialisateurApp;
        }

        public void execute(String[] args) {
            initialisateurApp.get().main(args);
        }
    }
}

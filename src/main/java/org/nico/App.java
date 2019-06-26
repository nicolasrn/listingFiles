package org.nico;

import org.nico.listingFiles.ListingFiles;
import org.nico.suppressionsredondances.SuppressionRedondance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static String TYPE;

    public static void main(String args[]) {
        try {
            args = parseCommandLine(args);
            Type.valueOf(TYPE).execute(args);
        } catch (Exception e) {
            LOG.error("erreur inatendue", e);
        }
    }

    private static String[] parseCommandLine(String[] args) {
        TYPE = args[0];
        return Stream.of(args).skip(1).toArray(String[]::new);
    }

    private enum Type {
        LISTING(new ListingFiles()),
        SUPPRESSION(new SuppressionRedondance());

        private ITypeApp app;

        Type(ITypeApp typeApp) {
            this.app = typeApp;
        }

        public void execute(String[] args) {
            app.main(args);
        }
    }
}

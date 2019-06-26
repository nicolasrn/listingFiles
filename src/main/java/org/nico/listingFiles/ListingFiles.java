package org.nico.listingFiles;

import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.nico.ITypeApp;
import org.nico.chrono.Chrono;
import org.nico.listingFiles.algo.EAlgo;
import org.nico.listingFiles.modele.filtre.FiltreParNom;
import org.nico.listingFiles.modele.wrapper.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListingFiles implements ITypeApp {

    private static final Logger LOG = LoggerFactory.getLogger(ListingFiles.class);
    public static Chrono chrono = Chrono.get();

    private static String ALGO = null;
    private static String PATH = null;
    private static String FILTRE = null;

    @Override
    public void main(String[] args) {
        try {
            initCommandLine(args);
            chrono.start("récupération des fichiers");
            List<File> files = getFiles(getPath());
            chrono.end();
            chrono.start("récupération des informations");
            Map<String, Wrapper> regrouppement = executeAlgo(files);
            chrono.end();
            chrono.start("écriture du résultat");
            enregistrer(mettreEnFormeResultat(regrouppement));
            chrono.end();
            LOG.debug("\n" + chrono.toString());
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    private static void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(getOptions(), args);
        ALGO = cmd.getOptionValue("a", EAlgo.DEFAUT.name());
        PATH = cmd.getOptionValue("p", ".");
        FILTRE = cmd.getOptionValue("f", FiltreParNom.DEFAUT.name());
    }

    private static Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option("a", true, "l'algo a utiliser"));
        opts.addOption(new Option("p", true, "le chemin depuis lequel lancer la recherche de doublon"));
        opts.addOption(new Option("f", true, "le filtre à appliquer"));
        return opts;
    }

    private static Path getPath() {
        return Paths.get(new File(PATH).toURI());
    }

    private static List<File> getFiles(Path path) {
        File current = path.toFile();
        List<File> files = new ArrayList<>();
        for (File child : current.listFiles()) {
            if (!child.getPath().contains("@") && !child.isHidden()) {
                if (child.isDirectory()) {
                    files.addAll(getFiles(child.toPath()));
                } else {
                    files.add(child);
                }
            }
        }
        return files;
    }

    private static Map<String, Wrapper> executeAlgo(List<File> files) {
        return EAlgo.valueOf(ALGO).execute(files, FILTRE);
    }

    private static String mettreEnFormeResultat(Map<String, Wrapper> pathParNom) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(pathParNom);
    }

    private static void enregistrer(String resultat) {
        File fResultat = new File("resultat-" + EAlgo.valueOf(ALGO).name().toLowerCase() + "-" + System.currentTimeMillis() + ".json");
        try (FileOutputStream fos = new FileOutputStream(fResultat); PrintWriter pw = new PrintWriter(fos)) {
            pw.print(resultat);
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }
}

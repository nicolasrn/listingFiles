package org.nico.listingFiles;

import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.nico.App;
import org.nico.ITypeApp;
import org.nico.chrono.Chrono;
import org.nico.listingFiles.algo.EAlgo;
import org.nico.listingFiles.modele.filtre.FiltreChecksum;
import org.nico.listingFiles.modele.filtre.FiltreParNom;
import org.nico.listingFiles.modele.wrapper.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListingFiles implements ITypeApp {

    private static final Logger LOG = LoggerFactory.getLogger(ListingFiles.class);

    private String ALGO = null;
    private String PATH = null;
    private String FILTRE = null;
    private List<File> fichiersSorties;

    public ListingFiles() {
        fichiersSorties = new ArrayList<>();
    }

    @Override
    public void main(String[] args) {
        try {
            initCommandLine(args);
            execute(ALGO, PATH, FILTRE);
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    public void execute(String algo, String path, String filtre) {
        try {
            Chrono.getInstance().start("récupération des fichiers");
            List<File> files = getFiles(getPath(path));
            Chrono.getInstance().end();
            Chrono.getInstance().start("récupération des informations");
            Map<String, Wrapper> regrouppement = executeAlgo(files, algo, filtre);
            Chrono.getInstance().end();
            Chrono.getInstance().start("écriture du résultat");
            enregistrer(mettreEnFormeResultat(regrouppement), algo);
            Chrono.getInstance().end();
            LOG.debug("\n" + Chrono.getInstance().toString());
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    private void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(getOptions(), args, true);
        if (cmd.hasOption("h")) {
            App.afficherAide("-" + App.Type.LISTING.name(), this::getOptions);
            System.exit(0);
        }
        ALGO = cmd.getOptionValue("a", EAlgo.DEFAUT.name());
        PATH = cmd.getOptionValue("p", ".");
        FILTRE = cmd.getOptionValue("f", FiltreParNom.DEFAUT.name());
    }

    private Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option(App.Type.LISTING.name(), false, "l'algo courant"));
        opts.addOption(new Option("a", "algo", true, "l'algo a utiliser " + listeValeursEnum(EAlgo.values())));
        opts.addOption(new Option("p", "path", true, "le chemin depuis lequel lancer la recherche de doublon"));
        opts.addOption(new Option("f", "filtre", true, "le filtre à appliquer " + getValeursFiltrePossible()));
        opts.addOption(new Option("h", "help", false, "affiche ce message d'aide"));
        return opts;
    }

    private String getValeursFiltrePossible() {
        return listeValeursEnum(Stream.concat(Stream.of(FiltreParNom.values()), Stream.of(FiltreChecksum.values())).distinct().toArray(Enum[]::new));
    }

    private String listeValeursEnum(Enum[] values) {
        return Stream.of(values).map(Enum::name).distinct().collect(Collectors.joining(", "));
    }

    private Path getPath(String path) {
        return Paths.get(new File(path).toURI());
    }

    private List<File> getFiles(Path path) {
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

    private Map<String, Wrapper> executeAlgo(List<File> files, String algo, String filtre) {
        return EAlgo.valueOf(algo).execute(files, filtre);
    }

    private String mettreEnFormeResultat(Map<String, Wrapper> pathParNom) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(pathParNom);
    }

    private void enregistrer(String resultat, String algo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY_HH-mm-ss");
        File fResultat = new File("resultat-" + EAlgo.valueOf(algo).name().toLowerCase() + "-" + simpleDateFormat.format(new Date()) + ".json");
        fichiersSorties.add(fResultat);
        try (FileOutputStream fos = new FileOutputStream(fResultat); PrintWriter pw = new PrintWriter(fos)) {
            pw.print(resultat);
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    public List<File> getFichiersGeneres() {
        return fichiersSorties;
    }
}

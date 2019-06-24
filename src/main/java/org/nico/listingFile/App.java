package org.nico.listingFile;

import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.nico.chrono.Chrono;
import org.nico.listingFile.modele.DescriptionFichier;
import org.nico.listingFile.modele.WrapperDescriptionFichiers;
import org.nico.listingFile.modele.filtre.Filtre;
import org.nico.listingFile.modele.filtre.FiltreChecksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static Chrono chrono = Chrono.get();

    private static String PATH = null;
    private static String FILTRE = null;

    public static void main(String[] args) {
        try {
            initCommandLine(args);
            chrono.start("récupération des fichiers");
            List<File> files = getFiles(getPath());
            chrono.end();
            chrono.start("récupération des informations");
            Map<String, WrapperDescriptionFichiers> pathParNom = files.stream()
                    .collect(Collectors.groupingBy(File::getName, Collectors.mapping(App::descriptionFichier, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), wrapperDescriptionFichiers(entry.getValue())))
                    .sorted(getComparatorFileWrapper())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            chrono.end();
            chrono.start("regrouppement des fichiers par checksum");
            Map<String, WrapperChecksum> fichiersParChecksum = files.stream()
                    .collect(Collectors.groupingBy(File::getName, Collectors.mapping(App::descriptionFichier, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> wrapperDescriptionFichiers(entry.getValue()))
                    .map(WrapperDescriptionFichiers::getFichiers)
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(DescriptionFichier::getChecksum))
                    .entrySet().stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), wrapperChecksum(entry.getValue())))
                    .sorted(getComparatorChecksumFileWrapper())
                    .filter(getFilterChecksum())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            chrono.end();
            chrono.start("structuration des informations");
            Map<String, Object> resultat = new HashMap<>();
            resultat.put("parNom", pathParNom);
            resultat.put("parChecksum", fichiersParChecksum);
            chrono.end();
            chrono.start("écriture du résultat");
            enregistrer(mettreEnFormeResultat(resultat));
            chrono.end();
            LOG.debug("\n" + chrono.toString());
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    private static void enregistrer(String resultat) {
        File fResultat = new File("resultat.json");
        try (FileOutputStream fos = new FileOutputStream(fResultat); PrintWriter pw = new PrintWriter(fos)) {
            pw.print(resultat);
        } catch (Exception e) {
            LOG.error("une erreur s'est produite : ", e);
        }
    }

    private static String mettreEnFormeResultat(Map<String, Object> pathParNom) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(pathParNom);
    }

    private static void initCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(getOptions(), args);
        PATH = cmd.getOptionValue("p", ".");
        FILTRE = cmd.getOptionValue("f", Filtre.DEFAUT.name());
    }

    private static Options getOptions() {
        Options opts = new Options();
        opts.addOption(new Option("p", true, "le chemin depuis lequel lancer la recherche de doublon"));
        opts.addOption(new Option("f", true, "le filtre à appliquer"));
        return opts;
    }

    private static Predicate<? super AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> getFilter() {
        return Filtre.valueOf(FILTRE).getFiltre();
    }

    private static Predicate<? super AbstractMap.SimpleEntry<String, WrapperChecksum>> getFilterChecksum() {
        return FiltreChecksum.valueOf(FILTRE).getFiltre();
    }


    private static WrapperChecksum wrapperChecksum(List<DescriptionFichier> descriptionFichiers) {
        return new WrapperChecksum(descriptionFichiers);
    }

    private static WrapperDescriptionFichiers wrapperDescriptionFichiers(List<DescriptionFichier> fileWrapper) {
        return new WrapperDescriptionFichiers(fileWrapper);
    }

    private static Comparator<? super AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> getComparatorFileWrapper() {
        Comparator<AbstractMap.SimpleEntry<String, WrapperDescriptionFichiers>> comparing = Comparator.comparing(a -> a.getValue().getOccurences());
        comparing = comparing.thenComparing(Map.Entry.comparingByKey()).reversed();
        return comparing;
    }

    private static Comparator<? super AbstractMap.SimpleEntry<String, WrapperChecksum>> getComparatorChecksumFileWrapper() {
        Comparator<AbstractMap.SimpleEntry<String, WrapperChecksum>> comparing = Comparator.comparing(a -> a.getValue().getOccurences());
        comparing = comparing.thenComparing(Map.Entry.comparingByKey()).reversed();
        return comparing;
    }

    private static DescriptionFichier descriptionFichier(File file) {
        return new DescriptionFichier(file);
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

    private static Path getPath() {
        return Paths.get(new File(PATH).toURI());
    }
}

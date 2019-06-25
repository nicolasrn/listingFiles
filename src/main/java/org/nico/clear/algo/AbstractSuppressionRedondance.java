package org.nico.clear.algo;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.nico.listingFile.modele.DescriptionFichier;
import org.nico.listingFile.modele.wrapper.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSuppressionRedondance<T extends Wrapper> {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractSuppressionRedondance.class);
    private final File repertoireDeplacement;

    protected AbstractSuppressionRedondance() {
        repertoireDeplacement = new File("./backup");
        if (!repertoireDeplacement.exists()) {
            if (!repertoireDeplacement.mkdirs()) {
                LOG.error("le repertoire de sauvegarde ne peut être créé");
                throw new RuntimeException("le répertoire de sauvegarde ne peut être créé");
            }
        }
    }

    public void analyserEtTraiter(File file) {
        Map<String, T> wrappers = getWrappers(file);
        wrappers.entrySet().stream()
                .map(Map.Entry::getValue)
                .map(Wrapper::getFichiers)
                .forEach(this::traiter);
    }

    protected Map<String, T> getWrappers(File file) {
        try {
            return new GsonBuilder().create().fromJson(new FileReader(file), getTypeMap());
        } catch (Exception e) {
            LOG.error("fichier non trouvé", e);
            throw new RuntimeException("Erreur lors de la transformation json vers objet", e);
        }
    }

    protected void traiter(List<DescriptionFichier> descriptionFichiers) {
        descriptionFichiers.stream()
                .filter(DescriptionFichier::isDoublon)
                .collect(Collectors.groupingBy(DescriptionFichier::getSize))
                .entrySet().stream()
                .map(Map.Entry::getValue)
                .forEach(fichiersMemeTaille -> {
                    fichiersMemeTaille.stream().skip(getNombreElementAIgnorer())
                            .map(DescriptionFichier::getPath)
                            .map(File::new)
                            .forEach(this::supprimer);
                });
    }

    protected abstract int getNombreElementAIgnorer();

    protected void supprimer(File file) {
        if (file.exists()) {
            File destination = new File(repertoireDeplacement, file.getName());
            try (FileInputStream input = new FileInputStream(file);
                 FileOutputStream output = new FileOutputStream(destination)) {
                IOUtils.copy(input, output);
            } catch (Exception e) {
                new RuntimeException("impossible de copier " + file + " dans " + destination);
            }
            if (!file.delete()) {
                LOG.error("le fichier " + file + " n'a pas pu être supprimé");
            }
        } else {
            LOG.info("le fichier " + file + " n'existe pas");
        }
    }

    protected abstract Type getTypeMap();
}

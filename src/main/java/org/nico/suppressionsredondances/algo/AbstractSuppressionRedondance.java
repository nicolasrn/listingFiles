package org.nico.suppressionsredondances.algo;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.nico.listingFiles.modele.DescriptionFichier;
import org.nico.listingFiles.modele.wrapper.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSuppressionRedondance<T extends Wrapper> {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractSuppressionRedondance.class);
    private final File repertoireDeplacement;
    private final boolean soft;

    protected AbstractSuppressionRedondance(String destination, boolean soft) {
        this.soft = soft;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY_HH-mm-ss");
        repertoireDeplacement = new File(destination + File.separator + "backup_" + simpleDateFormat.format(new Date()));
        if (!repertoireDeplacement.exists()) {
            if (!repertoireDeplacement.mkdirs()) {
                LOG.error("le repertoire de sauvegarde ne peut être créé");
                throw new RuntimeException("le répertoire de sauvegarde ne peut être créé");
            }
        }
        LOG.debug("soft: " + soft);
        LOG.debug("répertoire destination: " + repertoireDeplacement);
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
                .forEach(fichiersMemeTaille -> fichiersMemeTaille.stream().skip(getNombreElementAIgnorer()).forEach(this::supprimer));
    }

    protected abstract int getNombreElementAIgnorer();

    protected void supprimer(DescriptionFichier descriptionFichier) {
        File file = new File(descriptionFichier.getPath());
        if (file.exists()) {
            if (soft) {
                sauvegarderDansBackup(descriptionFichier, file);
            }
            if (!file.delete()) {
                LOG.error("le fichier " + file + " n'a pas pu être supprimé");
            }
        } else {
            LOG.info("le fichier " + file + " n'existe pas");
        }
    }

    private void sauvegarderDansBackup(DescriptionFichier descriptionFichier, File file) {
        File destination = new File(repertoireDeplacement, file.getName() + "_" + descriptionFichier.getSize() + "_" + descriptionFichier.getChecksum());
        try (FileInputStream input = new FileInputStream(file);
             FileOutputStream output = new FileOutputStream(destination)) {
            LOG.debug("deplacement de " + file + " vers " + destination);
            IOUtils.copy(input, output);
        } catch (Exception e) {
            new RuntimeException("impossible de copier " + file + " dans " + destination);
        }
    }

    protected abstract Type getTypeMap();
}

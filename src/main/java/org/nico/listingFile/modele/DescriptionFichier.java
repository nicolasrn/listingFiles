package org.nico.listingFile.modele;

import org.apache.commons.io.FileUtils;
import org.nico.listingFile.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DescriptionFichier {
    private final String path;
    private final String checksum;
    private final String size;
    private boolean doublon;

    public DescriptionFichier(File file) {
        this.size = FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file));
        this.path = file.getPath();
        this.checksum = computeChecksum(file);
    }

    private String computeChecksum(File file) {
        try {
            App.chrono.start("calcul checksum");
            String checksum = Objects.toString(FileUtils.checksumCRC32(file));
            App.chrono.end();
            return checksum;
        } catch (IOException e) {
            return "checksum non calculé";
        }
    }

    public String getChecksum() {
        return checksum;
    }

    public void setDoublon(boolean doublon) {
        this.doublon = doublon;
    }
}

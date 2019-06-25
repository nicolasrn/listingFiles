package org.nico.listingFiles.modele;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DescriptionFichier {
    private final String path;
    private final String checksum;
    private final long size;
    private final String humainSize;
    private boolean doublon;

    public DescriptionFichier(File file) {
        this.size = FileUtils.sizeOf(file);
        this.humainSize = FileUtils.byteCountToDisplaySize(size);
        this.path = file.getPath();
        this.checksum = computeChecksum(file);
    }

    private String computeChecksum(File file) {
        try {
            String checksum = Objects.toString(FileUtils.checksumCRC32(file));
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

    public boolean isDoublon() {
        return doublon;
    }

    public String getPath() {
        return path;
    }

    public Long getSize() {
        return size;
    }
}

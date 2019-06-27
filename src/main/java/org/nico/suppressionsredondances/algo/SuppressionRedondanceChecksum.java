package org.nico.suppressionsredondances.algo;

import com.google.gson.reflect.TypeToken;
import org.nico.listingFiles.modele.wrapper.WrapperChecksums;

import java.lang.reflect.Type;
import java.util.Map;

public class SuppressionRedondanceChecksum extends AbstractSuppressionRedondance<WrapperChecksums> {

    public SuppressionRedondanceChecksum(String destination, boolean soft) {
        super(destination, soft);
    }

    @Override
    protected int getNombreElementAIgnorer() {
        return 1;
    }

    @Override
    protected Type getTypeMap() {
        return new TypeToken<Map<String, WrapperChecksums>>() {
        }.getType();
    }
}
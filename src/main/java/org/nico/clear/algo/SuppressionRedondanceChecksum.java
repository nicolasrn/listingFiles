package org.nico.clear.algo;

import com.google.gson.reflect.TypeToken;
import org.nico.listingFile.modele.wrapper.WrapperChecksums;

import java.lang.reflect.Type;
import java.util.Map;

public class SuppressionRedondanceChecksum extends AbstractSuppressionRedondance<WrapperChecksums> {

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
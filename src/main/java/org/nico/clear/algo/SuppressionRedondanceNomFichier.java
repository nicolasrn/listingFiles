package org.nico.clear.algo;

import com.google.gson.reflect.TypeToken;
import org.nico.listingFile.modele.wrapper.WrapperParNomFichier;

import java.lang.reflect.Type;
import java.util.Map;

public class SuppressionRedondanceNomFichier extends AbstractSuppressionRedondance<WrapperParNomFichier> {

    @Override
    protected int getNombreElementAIgnorer() {
        return 1;
    }

    @Override
    protected Type getTypeMap() {
        return new TypeToken<Map<String, WrapperParNomFichier>>() {
        }.getType();
    }
}

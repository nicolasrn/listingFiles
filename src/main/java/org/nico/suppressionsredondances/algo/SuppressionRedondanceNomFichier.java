package org.nico.suppressionsredondances.algo;

import com.google.gson.reflect.TypeToken;
import org.nico.listingFiles.modele.wrapper.WrapperParNomFichier;

import java.lang.reflect.Type;
import java.util.Map;

public class SuppressionRedondanceNomFichier extends AbstractSuppressionRedondance<WrapperParNomFichier> {

    public SuppressionRedondanceNomFichier(String destination) {
        super(destination);
    }

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

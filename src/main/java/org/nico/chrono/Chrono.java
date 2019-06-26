package org.nico.chrono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

public class Chrono {

    public final static Chrono chrono = new Chrono();
    private static final Logger LOG = LoggerFactory.getLogger(Chrono.class);
    private Deque<Mesure> temps;
    private Deque<Mesure> enCours;

    private Chrono() {
        temps = new ArrayDeque<>();
        enCours = new ArrayDeque<>();
    }

    public static Chrono getInstance() {
        return chrono;
    }

    public void start(String name) {
        LOG.debug("début " + name);
        enCours.push(new Mesure(name));
    }

    public void end() {
        Mesure mesure = enCours.pop();
        mesure.finalise();
        temps.offer(mesure);
        LOG.debug("finalisation de " + mesure.nom + " : " + mesure.getDuree() + " ms");
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Mesure mesure : temps) {
            str.append(mesure.nom).append(" ").append(mesure.getDuree()).append(" ms\n");
        }
        return str.toString();
    }

    private static class Mesure {
        private String nom;
        private long start;
        private long end;

        private Mesure(String nom) {
            this.nom = nom;
            start = current();
        }

        private void finalise() {
            this.end = current();
        }

        private long getDuree() {
            return end - start;
        }

        private long current() {
            return System.currentTimeMillis();
        }
    }
}

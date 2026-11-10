package tn.esprit.autoloc.io;

import tn.esprit.autoloc.domain.Agence;

public class RapportTotal implements Rapport {

    @Override
    public String generer(Agence agence, double total) {
        return "Total : " + total + "\n";
    }
}
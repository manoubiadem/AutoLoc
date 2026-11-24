
package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;

@Component("standard")
public class StandardTarifStrategy implements TarifStrategy {

    @Override
    public double calculer(double tarifJournalier, int nbJours) {
        return tarifJournalier * nbJours;
    }
}

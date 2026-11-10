package tn.esprit.autoloc.io;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;

@Component
public class RapportConsoleDecorator implements Rapport {

    private final Rapport rapport = new RapportTotal();

    @Override
    public String generer(Agence agence, double total) {

        System.out.println(
                "Id : " + agence.getIdAgence()
                        + ", Nom : " + agence.getNom()
                        + ", Tel : " + agence.getTelephone()
        );

        return rapport.generer(agence, total);
    }
}
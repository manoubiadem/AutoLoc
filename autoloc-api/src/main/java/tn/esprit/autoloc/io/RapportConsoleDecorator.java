
package tn.esprit.autoloc.io;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.autoloc.domain.Agence;

@Component
public class RapportConsoleDecorator implements Rapport {

    private static final Logger logger =
            LoggerFactory.getLogger(RapportConsoleDecorator.class);

    private final Rapport rapport = new RapportTotal();

    @Override
    public String generer(Agence agence, double total) {

        logger.info(
                "Id : {}, Nom : {}, Tel : {}",
                agence.getIdAgence(),
                agence.getNom(),
                agence.getTelephone()
        );

        return rapport.generer(agence, total);
    }
}

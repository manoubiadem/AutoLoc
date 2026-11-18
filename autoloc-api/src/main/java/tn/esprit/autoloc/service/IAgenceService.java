
package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;

public interface IAgenceService
        extends IGenericService<Agence, Long> {

    boolean hasVehicules(Long id);

    Agence modifierAgenceAvecEmployes(
            Long idAgence,
            Agence donnees
    );
}

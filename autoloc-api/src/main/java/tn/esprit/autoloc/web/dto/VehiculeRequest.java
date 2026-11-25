package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;

/** Associations are managed through the dedicated vehicle assignment endpoints. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VehiculeRequest(String immatriculation, String marque, String modele,
                              CategorieVehicule categorie, BigDecimal tarifJournalier,
                              StatutVehicule statut) {

    public Vehicule toEntity() {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMarque(marque);
        vehicule.setModele(modele);
        vehicule.setCategorie(categorie);
        vehicule.setTarifJournalier(tarifJournalier);
        vehicule.setStatut(statut);
        return vehicule;
    }
}

package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Equipement;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EquipementRequest(String libelle) {

    public Equipement toEntity() {
        Equipement equipement = new Equipement();
        equipement.setLibelle(libelle);
        return equipement;
    }
}

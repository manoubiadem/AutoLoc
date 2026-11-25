package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Agence;

import java.util.List;

/** Cet endpoint ajoute uniquement de nouveaux employes a l'agence designee par l'URL. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgenceAvecEmployesRequest(
        String nom, String ville, String adresse, String telephone,
        List<EmployeRequest> employes) {

    public Agence toEntity() {
        Agence agence = new AgenceRequest(nom, ville, adresse, telephone).toEntity();
        if (employes != null) {
            agence.setEmployes(employes.stream().map(EmployeRequest::toEntity).toList());
        }
        return agence;
    }
}

package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Agence;

/** Informations modifiables ; identifiant, effectif et associations restent au serveur. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgenceRequest(String nom, String ville, String adresse, String telephone) {

    public Agence toEntity() {
        Agence agence = new Agence();
        agence.setNom(nom);
        agence.setVille(ville);
        agence.setAdresse(adresse);
        agence.setTelephone(telephone);
        return agence;
    }
}

package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Employe;

/** The role is a business job description, not an authentication authority. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeRequest(String nom, String prenom, String role) {

    public Employe toEntity() {
        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setRole(role);
        return employe;
    }
}

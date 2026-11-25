package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/** Le vehicule est une reference existante verifiee, jamais un objet a persister en cascade. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MaintenanceRequest(
        LocalDate dateDebut, LocalDate dateFin, String description, Long vehiculeId) {
}

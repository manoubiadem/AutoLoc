package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/** Seules des references existantes peuvent etre affectees ; aucun graphe JPA entrant. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationRequest(
        LocalDate dateDebut, LocalDate dateFin, String statut,
        Long vehiculeId, Long clientId, Long contratId) {
}

package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Contrat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Donnees metier du contrat ; ni paiements ni reservation ne sont modifiables ici. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContratRequest(LocalDate dateSignature, BigDecimal montantTotal, boolean valide) {

    public Contrat toEntity() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(montantTotal);
        contrat.setValide(valide);
        return contrat;
    }
}

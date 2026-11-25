package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.autoloc.domain.Client;

/** Writable client details; registration date and reservations remain server-owned. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientRequest(String nom, String prenom, String email,
                            String telephone, String numPermis) {

    public Client toEntity() {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone(telephone);
        client.setNumPermis(numPermis);
        return client;
    }
}

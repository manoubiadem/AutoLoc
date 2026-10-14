package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;

@Service
public class ClientServiceImpl
        extends GenericServiceImpl<Client, Long>
        implements IClientService {

    public ClientServiceImpl(IClientRepository repository) {
        super(repository);
    }

    @Override
    public boolean check(Client client, boolean isNew) {
        return client != null;
    }

    @Override
    protected String getEntityName() {
        return "Client";
    }

    @Override
    protected Long getId(Client client) {
        return client.getIdClient();
    }

    @Override
    protected void setFields(Client source, Client target) {
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setEmail(source.getEmail());
        target.setTelephone(source.getTelephone());
        target.setNumPermis(source.getNumPermis());
        target.setDateInscription(source.getDateInscription());
        target.setReservations(source.getReservations());
    }
}
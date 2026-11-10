package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.calcul.TarifStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.Map;

@Service
public class VehiculeServiceImpl
        extends GenericServiceImpl<Vehicule, Long>
        implements IVehiculeService {

    private final Map<String, TarifStrategy> strategies;

    public VehiculeServiceImpl(
            IVehiculeRepository repository,
            Map<String, TarifStrategy> strategies) {

        super(repository);
        this.strategies = strategies;
    }

    @Override
    public boolean check(Vehicule vehicule, boolean isNew) {
        return vehicule != null;
    }

    @Override
    protected String getEntityName() {
        return "Vehicule";
    }

    @Override
    protected Long getId(Vehicule vehicule) {
        return vehicule.getIdVehicule();
    }

    @Override
    protected void setFields(Vehicule source, Vehicule target) {

        target.setImmatriculation(source.getImmatriculation());
        target.setMarque(source.getMarque());
        target.setModele(source.getModele());
        target.setCategorie(source.getCategorie());
        target.setTarifJournalier(source.getTarifJournalier());
        target.setStatut(source.getStatut());
        target.setAgence(source.getAgence());
        target.setReservations(source.getReservations());
        target.setEquipements(source.getEquipements());
    }

    @Override
    public double calculTarif(
            Vehicule vehicule,
            int nbJours,
            VehiculeOffre offre) {

        TarifStrategy strategy = strategies.get(offre.name());

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Offre non supportée : " + offre
            );
        }

        return strategy.calculer(
                vehicule.getTarifJournalier().doubleValue(),
                nbJours
        );
    }
}
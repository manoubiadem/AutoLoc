package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@Primary
public class AgenceServiceImpl
        extends GenericServiceImpl<Agence, Long>
        implements IAgenceService, ISimulationAgenceService {

    private final IVehiculeRepository vehiculeRepository;
    private final IVehiculeService vehiculeService;
    private final RapportFactory rapportFactory;

    public AgenceServiceImpl(
            IAgenceRepository agenceRepository,
            IVehiculeRepository vehiculeRepository,
            IVehiculeService vehiculeService,
            RapportFactory rapportFactory) {

        super(agenceRepository);

        this.vehiculeRepository = vehiculeRepository;
        this.vehiculeService = vehiculeService;
        this.rapportFactory = rapportFactory;
    }

    @Override
    public boolean check(
            Agence agence,
            boolean isNew) {

        return agence != null
                && Utils.isNotEmpty(agence.getNom())
                && Utils.isNotEmpty(agence.getVille())
                && Utils.isNotEmpty(agence.getAdresse())
                && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    protected String getEntityName() {
        return "Agence";
    }

    @Override
    protected Long getId(Agence agence) {
        return agence.getIdAgence();
    }

    @Override
    protected void setFields(
            Agence source,
            Agence target) {

        target.setNom(source.getNom());
        target.setVille(source.getVille());
        target.setAdresse(source.getAdresse());
        target.setTelephone(source.getTelephone());
        target.setVehicules(source.getVehicules());
    }

    @Override
    public boolean hasVehicules(Long id) {

        Agence agence = findById(id);

        return agence.getVehicules() != null
                && !agence.getVehicules().isEmpty();
    }

    @Override
    public void deleteById(Long id) {

        Agence agence = findById(id);

        if (agence.getVehicules() != null) {

            agence.getVehicules().forEach(
                    vehicule -> vehicule.setAgence(null)
            );

            vehiculeRepository.saveAll(
                    agence.getVehicules()
            );
        }

        super.deleteById(id);
    }

    @Override
    public String simuler(
            Agence agence,
            int nbJours,
            VehiculeOffre offre) {

        double total = 0;

        for (Vehicule vehicule : agence.getVehicules()) {

            total += vehiculeService.calculTarif(
                    vehicule,
                    nbJours,
                    offre
            );
        }

        return rapportFactory
                .creerRapport()
                .generer(agence, total);
    }
}
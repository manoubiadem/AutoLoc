package tn.esprit.autoloc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.calcul.TarifStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.ArrayList;
import java.util.Map;

@Service
public class VehiculeServiceImpl
        extends GenericServiceImpl<Vehicule, Long>
        implements IVehiculeService {

    private static final Logger logger =
            LoggerFactory.getLogger(VehiculeServiceImpl.class);

    private final Map<String, TarifStrategy> strategies;
    private final IAgenceRepository agenceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;

    public VehiculeServiceImpl(
            IVehiculeRepository repository,
            IAgenceRepository agenceRepository,
            IEquipementRepository equipementRepository,
            Map<String, TarifStrategy> strategies) {

        super(repository);
        this.vehiculeRepository = repository;
        this.agenceRepository = agenceRepository;
        this.equipementRepository = equipementRepository;
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
    }

    @Override
    public double calculTarif(
            Vehicule vehicule,
            int nbJours,
            VehiculeOffre offre) {

        // Atelier 10 - Correction du nom du bean
        String nomBean = offre.name();

        if ("LONGUE_DUREE".equals(nomBean)) {
            nomBean = "longueDuree";
        }

        if ("WEEKEND".equals(nomBean)) {
            nomBean = "weekend";
        }

        if ("STANDARD".equals(nomBean)) {
            nomBean = "standard";
        }

        TarifStrategy strategy = strategies.get(nomBean);

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

    @Override
    @Transactional
    public void desaffecterAgence(Long idVehicule) {
        Vehicule vehicule = findById(idVehicule);
        vehicule.setAgence(null);
    }

    @Override
    @Transactional
    public void affecterAgence(Long idVehicule, Long idAgence) {

        Vehicule vehicule = findById(idVehicule);

        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agence",
                                String.valueOf(idAgence)
                        )
                );

        vehicule.setAgence(agence);
        vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public void affecterEquipement(
            Long idVehicule,
            Long idEquipement) {

        Vehicule vehicule = findById(idVehicule);

        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Equipement",
                                String.valueOf(idEquipement)
                        )
                );

        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }

        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        logger.info(">>> SUPPRESSION VEHICULE : {}", id);

        Vehicule vehicule = findById(id);

        // Retirer le vehicule de la liste de son agence
        Agence agence = vehicule.getAgence();

        if (agence != null) {
            if (agence.getVehicules() != null) {
                agence.getVehicules().removeIf(
                        v -> id.equals(v.getIdVehicule())
                );
            }

            vehicule.setAgence(null);
        }

        // Supprimer le vehicule
        vehiculeRepository.delete(vehicule);

        // Executer les modifications SQL
        vehiculeRepository.flush();

        boolean existe = vehiculeRepository.existsById(id);

        logger.info(">>> EXISTE APRES FLUSH : {}", existe);

        if (existe) {
            throw new IllegalStateException(
                    "La suppression du vehicule " + id + " a echoue"
            );
        }
    }
}


package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class AgenceServiceImpl
        extends GenericServiceImpl<Agence, Long>
        implements IAgenceService, ISimulationAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IVehiculeService vehiculeService;
    private final IEmployeService employeService;
    private final RapportFactory rapportFactory;

    public AgenceServiceImpl(
            IAgenceRepository agenceRepository,
            IVehiculeRepository vehiculeRepository,
            IVehiculeService vehiculeService,
            IEmployeService employeService,
            RapportFactory rapportFactory) {

        super(agenceRepository);

        this.agenceRepository = agenceRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.vehiculeService = vehiculeService;
        this.employeService = employeService;
        this.rapportFactory = rapportFactory;
    }

    @Override
    public boolean check(Agence agence, boolean isNew) {

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
    protected void setFields(Agence source, Agence target) {

        target.setNom(source.getNom());
        target.setVille(source.getVille());
        target.setAdresse(source.getAdresse());
        target.setTelephone(source.getTelephone());
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

    // Question 12 : modifier une agence
    // et enregistrer ses nouveaux employes

    @Override
    @Transactional
    public Agence modifierAgenceAvecEmployes(
            Long idAgence,
            Agence donnees) {

        Agence agence = findById(idAgence);

        if (!check(donnees, false)) {
            throw new IllegalArgumentException(
                    "Agence non valide"
            );
        }

        // Modifier les informations de l'agence
        agence.setNom(donnees.getNom());
        agence.setVille(donnees.getVille());
        agence.setAdresse(donnees.getAdresse());
        agence.setTelephone(donnees.getTelephone());

        // Enregistrer les modifications de l'agence
        agenceRepository.save(agence);

        // Ajouter les nouveaux employes
        if (donnees.getEmployes() != null) {

            for (Employe employe : donnees.getEmployes()) {

                // Synchroniser les deux cotes
                agence.ajouterEmploye(employe);

                // Enregistrer avec le service Employe
                employeService.create(employe);
            }
        }

        // Calculer l'effectif de l'agence

        // Atelier 10 : anomalie volontaire S1854
        int nombreEmployes = 0;

        nombreEmployes =
                agence.getEmployes() == null
                        ? 0
                        : agence.getEmployes().size();

        agence.setEffectif(nombreEmployes);

        // Enregistrer le nouvel effectif
        return agenceRepository.save(agence);
    }
}

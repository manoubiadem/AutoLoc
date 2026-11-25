package tn.esprit.autoloc.autolocapi;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MaintenancePersistenceTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private IVehiculeRepository vehiculeRepository;
    @Autowired
    private IMaintenanceRepository maintenanceRepository;

    @Test
    void nouvelleMaintenanceReferenceUnVehiculeExistantDetacheSansLePersister() {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(UUID.randomUUID().toString().substring(0, 20));
        vehicule.setMarque("Marque conservee");
        vehicule.setModele("Modele conserve");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("100.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehiculeRepository.saveAndFlush(vehicule);
        Long vehiculeId = vehicule.getIdVehicule();
        entityManager.detach(vehicule);
        assertFalse(entityManager.contains(vehicule));

        Maintenance maintenance = new Maintenance();
        maintenance.setDescription("Revision");
        maintenance.setVehicule(vehicule);
        maintenanceRepository.saveAndFlush(maintenance);
        Long maintenanceId = maintenance.getIdMaintenance();
        entityManager.clear();

        Maintenance saved = maintenanceRepository.findById(maintenanceId).orElseThrow();
        assertEquals(vehiculeId, saved.getVehicule().getIdVehicule());
        assertEquals("Marque conservee", saved.getVehicule().getMarque());
        assertEquals(StatutVehicule.DISPONIBLE, saved.getVehicule().getStatut());
    }
}

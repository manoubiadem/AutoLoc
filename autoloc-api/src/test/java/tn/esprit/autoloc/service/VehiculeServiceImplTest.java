package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.calcul.TarifStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehiculeServiceImplTest {

    private IVehiculeRepository repository;
    private IAgenceRepository agenceRepository;
    private IEquipementRepository equipementRepository;
    private Map<String, TarifStrategy> strategies;
    private VehiculeServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IVehiculeRepository.class);
        agenceRepository = mock(IAgenceRepository.class);
        equipementRepository = mock(IEquipementRepository.class);
        strategies = new HashMap<>();
        service = new VehiculeServiceImpl(repository, agenceRepository,
                equipementRepository, strategies);
    }

    @Test
    void checkAccepteVehicule() {
        assertTrue(service.check(mock(Vehicule.class), true));
    }

    @Test
    void checkRefuseNull() {
        assertFalse(service.check(null, true));
    }

    @Test
    void createEnregistreVehicule() {
        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getIdVehicule()).thenReturn((Long) null);
        when(repository.save(vehicule)).thenReturn(vehicule);
        assertSame(vehicule, service.create(vehicule));
    }

    @Test
    void createRefuseIdRenseigne() {
        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getIdVehicule()).thenReturn(7L);
        assertThrows(IllegalArgumentException.class, () -> service.create(vehicule));
    }

    @Test
    void findByIdRetourneVehicule() {
        Vehicule vehicule = mock(Vehicule.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        assertSame(vehicule, service.findById(7L));
    }

    @Test
    void findByIdSignaleVehiculeAbsent() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void findAllRetourneListe() {
        List<Vehicule> liste = List.of(mock(Vehicule.class));
        when(repository.findAll()).thenReturn(liste);
        assertSame(liste, service.findAll());
    }

    @Test
    void updateCopieImmatriculation() {
        Vehicule source = mock(Vehicule.class);
        Vehicule existing = mock(Vehicule.class);
        when(source.getImmatriculation()).thenReturn("100 TU 111");
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        assertSame(existing, service.update(7L, source));
        verify(existing).setImmatriculation("100 TU 111");
    }

    @Test
    void calculTarifStandard() {
        TarifStrategy strategie = mock(TarifStrategy.class);
        strategies.put("standard", strategie);
        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getTarifJournalier()).thenReturn(BigDecimal.valueOf(100));
        when(strategie.calculer(100.0, 3)).thenReturn(300.0);
        assertEquals(300.0, service.calculTarif(vehicule, 3, VehiculeOffre.STANDARD));
    }

    @Test
    void calculTarifWeekend() {
        TarifStrategy strategie = mock(TarifStrategy.class);
        strategies.put("weekend", strategie);
        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getTarifJournalier()).thenReturn(BigDecimal.valueOf(100));
        when(strategie.calculer(100.0, 2)).thenReturn(160.0);
        assertEquals(160.0, service.calculTarif(vehicule, 2, VehiculeOffre.WEEKEND));
    }

    @Test
    void calculTarifLongueDuree() {
        TarifStrategy strategie = mock(TarifStrategy.class);
        strategies.put("longueDuree", strategie);
        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getTarifJournalier()).thenReturn(BigDecimal.valueOf(100));
        when(strategie.calculer(100.0, 10)).thenReturn(750.0);
        assertEquals(750.0, service.calculTarif(vehicule, 10, VehiculeOffre.LONGUE_DUREE));
    }

    @Test
    void calculTarifRefuseStrategieAbsente() {
        Vehicule vehicule = mock(Vehicule.class);
        assertThrows(IllegalArgumentException.class,
                () -> service.calculTarif(vehicule, 2, VehiculeOffre.STANDARD));
    }

    @Test
    void desaffecterAgenceMetAgenceANull() {
        Vehicule vehicule = mock(Vehicule.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        service.desaffecterAgence(7L);
        verify(vehicule).setAgence(null);
    }

    @Test
    void affecterAgenceEnregistreAssociation() {
        Vehicule vehicule = mock(Vehicule.class);
        Agence agence = mock(Agence.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(agence));
        service.affecterAgence(7L, 2L);
        verify(vehicule).setAgence(agence);
        verify(repository).save(vehicule);
    }

    @Test
    void affecterAgenceRefuseAgenceAbsente() {
        Vehicule vehicule = mock(Vehicule.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(agenceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.affecterAgence(7L, 99L));
    }

    @Test
    void affecterEquipementAjouteEquipement() {
        Vehicule vehicule = mock(Vehicule.class);
        Equipement equipement = mock(Equipement.class);
        List<Equipement> liste = new ArrayList<>();
        when(vehicule.getEquipements()).thenReturn(liste);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(3L)).thenReturn(Optional.of(equipement));
        service.affecterEquipement(7L, 3L);
        assertTrue(liste.contains(equipement));
    }

    @Test
    void affecterEquipementNeDupliquePasAssociation() {
        Vehicule vehicule = mock(Vehicule.class);
        Equipement equipement = mock(Equipement.class);
        List<Equipement> liste = new ArrayList<>(List.of(equipement));
        when(vehicule.getEquipements()).thenReturn(liste);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(3L)).thenReturn(Optional.of(equipement));
        service.affecterEquipement(7L, 3L);
        assertEquals(1, liste.size());
    }

    @Test
    void deleteByIdSupprimeVehiculeSansAgence() {
        Vehicule vehicule = mock(Vehicule.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(repository.existsById(7L)).thenReturn(false);
        service.deleteById(7L);
        verify(repository).delete(vehicule);
        verify(repository).flush();
    }

    @Test
    void deleteByIdSignaleSuppressionNonEffectuee() {
        Vehicule vehicule = mock(Vehicule.class);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicule));
        when(repository.existsById(7L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.deleteById(7L));
    }
}

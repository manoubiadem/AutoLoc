package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.io.Rapport;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgenceServiceImplTest {

    private IAgenceRepository repository;
    private IVehiculeRepository vehiculeRepository;
    private IVehiculeService vehiculeService;
    private IEmployeService employeService;
    private RapportFactory rapportFactory;
    private AgenceServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IAgenceRepository.class);
        vehiculeRepository = mock(IVehiculeRepository.class);
        vehiculeService = mock(IVehiculeService.class);
        employeService = mock(IEmployeService.class);
        rapportFactory = mock(RapportFactory.class);
        service = new AgenceServiceImpl(repository, vehiculeRepository,
                vehiculeService, employeService, rapportFactory);
    }

    private Agence agenceValide() {
        Agence agence = mock(Agence.class);
        when(agence.getNom()).thenReturn("Agence Centre");
        when(agence.getVille()).thenReturn("Tunis");
        when(agence.getAdresse()).thenReturn("Rue Centrale");
        when(agence.getTelephone()).thenReturn("12345678");
        return agence;
    }

    @Test
    void checkAccepteAgenceValide() {
        assertTrue(service.check(agenceValide(), true));
    }

    @Test
    void checkRefuseAgenceNulle() {
        assertFalse(service.check(null, true));
    }

    @Test
    void checkRefuseVilleVide() {
        Agence agence = agenceValide();
        when(agence.getVille()).thenReturn(" ");
        assertFalse(service.check(agence, true));
    }

    @Test
    void createEnregistreAgenceValide() {
        Agence agence = agenceValide();
        when(agence.getIdAgence()).thenReturn((Long) null);
        when(repository.save(agence)).thenReturn(agence);
        assertSame(agence, service.create(agence));
    }

    @Test
    void createRefuseIdentifiantRenseigne() {
        Agence agence = agenceValide();
        when(agence.getIdAgence()).thenReturn(4L);
        assertThrows(IllegalArgumentException.class, () -> service.create(agence));
    }

    @Test
    void findByIdRetourneAgence() {
        Agence agence = agenceValide();
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        assertSame(agence, service.findById(1L));
    }

    @Test
    void findByIdSignaleAgenceAbsente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void findAllRetourneAgences() {
        List<Agence> agences = List.of(agenceValide());
        when(repository.findAll()).thenReturn(agences);
        assertSame(agences, service.findAll());
    }

    @Test
    void hasVehiculesRetourneTrueQuandNonVide() {
        Agence agence = mock(Agence.class);
        when(agence.getVehicules()).thenReturn(List.of(mock(Vehicule.class)));
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        assertTrue(service.hasVehicules(1L));
    }

    @Test
    void hasVehiculesRetourneFalseSiNull() {
        Agence agence = mock(Agence.class);
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        assertFalse(service.hasVehicules(1L));
    }

    @Test
    void deleteByIdSupprimeAgenceSansVehicules() {
        Agence agence = mock(Agence.class);
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteByIdDetacheLesVehicules() {
        Agence agence = mock(Agence.class);
        Vehicule vehicule = mock(Vehicule.class);
        List<Vehicule> liste = new ArrayList<>(List.of(vehicule));
        when(agence.getVehicules()).thenReturn(liste);
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteById(1L);
        verify(vehicule).setAgence(null);
        verify(vehiculeRepository).saveAll(liste);
        verify(repository).deleteById(1L);
    }

    @Test
    void simulerAdditionneLesTarifs() {
        Agence agence = mock(Agence.class);
        Vehicule premier = mock(Vehicule.class);
        Vehicule second = mock(Vehicule.class);
        when(agence.getVehicules()).thenReturn(List.of(premier, second));
        when(vehiculeService.calculTarif(premier, 3, VehiculeOffre.STANDARD)).thenReturn(120.0);
        when(vehiculeService.calculTarif(second, 3, VehiculeOffre.STANDARD)).thenReturn(180.0);
        Rapport rapport = mock(Rapport.class);
        when(rapportFactory.creerRapport()).thenReturn(rapport);
        when(rapport.generer(agence, 300.0)).thenReturn("Total : 300");
        assertEquals("Total : 300", service.simuler(agence, 3, VehiculeOffre.STANDARD));
    }

    @Test
    void modifierAgenceAvecEmployesRefuseDonneesInvalides() {
        Agence agence = mock(Agence.class);
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        assertThrows(IllegalArgumentException.class,
                () -> service.modifierAgenceAvecEmployes(1L, null));
    }

    @Test
    void modifierAgenceAvecEmployesModifieInformationsEtEffectif() {
        Agence agence = mock(Agence.class);
        Agence donnees = agenceValide();
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        when(repository.save(agence)).thenReturn(agence);
        assertSame(agence, service.modifierAgenceAvecEmployes(1L, donnees));
        verify(agence).setNom("Agence Centre");
        verify(agence).setVille("Tunis");
        verify(agence).setEffectif(0);
        verify(repository, times(2)).save(agence);
    }
}

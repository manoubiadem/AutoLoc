package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasicAgenceServiceImplTest {

    private IAgenceRepository repository;
    private AgenceServiceImpl agenceService;
    private BasicAgenceServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IAgenceRepository.class);
        agenceService = mock(AgenceServiceImpl.class);
        service = new BasicAgenceServiceImpl(repository, agenceService);
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
    void checkRefuseNomVide() {
        Agence agence = agenceValide();
        when(agence.getNom()).thenReturn(" ");
        assertFalse(service.check(agence, true));
    }

    @Test
    void createEnregistreAgenceValide() {
        Agence agence = agenceValide();
        when(agence.getIdAgence()).thenReturn((Long) null);
        when(repository.save(agence)).thenReturn(agence);
        assertSame(agence, service.create(agence));
        verify(repository).save(agence);
    }

    @Test
    void createRefuseIdentifiantExistant() {
        Agence agence = agenceValide();
        when(agence.getIdAgence()).thenReturn(2L);
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
    void findAllRetourneListe() {
        List<Agence> agences = List.of(agenceValide());
        when(repository.findAll()).thenReturn(agences);
        assertSame(agences, service.findAll());
    }

    @Test
    void updateMetAJourAgence() {
        Agence source = agenceValide();
        Agence existing = mock(Agence.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        assertSame(existing, service.update(1L, source));
        verify(existing).setNom("Agence Centre");
        verify(existing).setVille("Tunis");
        verify(repository).save(existing);
    }

    @Test
    void deleteByIdSupprimeAgenceExistante() {
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteByIdSignaleAgenceAbsente() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(99L));
    }

    @Test
    void hasVehiculesRetourneFalseDansImplementationBasique() {
        assertFalse(service.hasVehicules(1L));
    }

    @Test
    void modifierAgenceAvecEmployesDelegueAuService() {
        Agence donnees = agenceValide();
        Agence resultat = mock(Agence.class);
        when(agenceService.modifierAgenceAvecEmployes(1L, donnees)).thenReturn(resultat);
        assertSame(resultat, service.modifierAgenceAvecEmployes(1L, donnees));
        verify(agenceService).modifierAgenceAvecEmployes(1L, donnees);
    }
}

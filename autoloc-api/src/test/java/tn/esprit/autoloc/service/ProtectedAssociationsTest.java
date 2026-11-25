package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.AgenceAvecEmployesRequest;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.EmployeRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProtectedAssociationsTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void modifierAgenceConserveIdentifiantRelationsEtEffectif(boolean basic) {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        Agence existing = new Agence();
        existing.setIdAgence(7L);
        List<Vehicule> vehicules = new ArrayList<>(List.of(new Vehicule()));
        List<Employe> employes = new ArrayList<>(List.of(new Employe()));
        existing.setVehicules(vehicules);
        existing.setEmployes(employes);
        existing.setEffectif(1);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        AgenceServiceImpl fullService = agenceService(repository, mock(IEmployeService.class));
        IAgenceService service = basic
                ? new BasicAgenceServiceImpl(repository, fullService)
                : fullService;
        Agence source = new AgenceRequest("Centre", "Tunis", "Rue Centrale", "12345678").toEntity();

        assertSame(existing, service.update(7L, source));

        assertEquals(7L, existing.getIdAgence());
        assertEquals("Centre", existing.getNom());
        assertEquals("Tunis", existing.getVille());
        assertEquals("Rue Centrale", existing.getAdresse());
        assertEquals("12345678", existing.getTelephone());
        assertSame(vehicules, existing.getVehicules());
        assertSame(employes, existing.getEmployes());
        assertEquals(1, existing.getEffectif());
        verify(repository).save(existing);
    }

    @Test
    void modifierContratConserveIdentifiantPaiementsEtReservation() {
        IContratRepository repository = mock(IContratRepository.class);
        Contrat existing = new Contrat();
        existing.setIdContrat(7L);
        List<Paiement> paiements = new ArrayList<>(List.of(new Paiement()));
        Reservation reservation = new Reservation();
        existing.setPaiements(paiements);
        existing.setReservation(reservation);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        LocalDate signature = LocalDate.of(2026, 11, 25);
        Contrat source = new ContratRequest(signature, new BigDecimal("250.00"), true).toEntity();

        assertSame(existing, new ContratServiceImpl(repository).update(7L, source));

        assertEquals(7L, existing.getIdContrat());
        assertEquals(signature, existing.getDateSignature());
        assertEquals(new BigDecimal("250.00"), existing.getMontantTotal());
        assertTrue(existing.isValide());
        assertSame(paiements, existing.getPaiements());
        assertSame(reservation, existing.getReservation());
        verify(repository).save(existing);
    }

    @Test
    void modifierAvecEmployesAjouteDeNouveauxEmployesEtCalculeEffectif() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        IEmployeRepository employeRepository = mock(IEmployeRepository.class);
        EmployeServiceImpl employeService = new EmployeServiceImpl(employeRepository);
        Agence existing = new Agence();
        existing.setIdAgence(7L);
        Employe ancienEmploye = new Employe();
        ancienEmploye.setIdEmploye(8L);
        existing.ajouterEmploye(ancienEmploye);
        List<Vehicule> vehicules = new ArrayList<>(List.of(new Vehicule()));
        existing.setVehicules(vehicules);
        existing.setEffectif(1);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(employeRepository.save(any(Employe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Agence source = new AgenceAvecEmployesRequest("Centre", "Tunis", "Rue Centrale", "12345678",
                List.of(new EmployeRequest("Nouveau", "Employe", "Agent"))).toEntity();

        Agence result = agenceService(repository, employeService).modifierAgenceAvecEmployes(7L, source);

        ArgumentCaptor<Employe> captor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository).save(captor.capture());
        Employe nouvelEmploye = captor.getValue();
        assertNull(nouvelEmploye.getIdEmploye());
        assertEquals("Nouveau", nouvelEmploye.getNom());
        assertEquals("Employe", nouvelEmploye.getPrenom());
        assertEquals("Agent", nouvelEmploye.getRole());
        assertSame(existing, nouvelEmploye.getAgence());
        assertSame(existing, ancienEmploye.getAgence());
        assertEquals(List.of(ancienEmploye, nouvelEmploye), result.getEmployes());
        assertSame(vehicules, result.getVehicules());
        assertEquals(7L, result.getIdAgence());
        assertEquals(2, result.getEffectif());
        verify(repository, times(2)).save(existing);
    }

    private AgenceServiceImpl agenceService(IAgenceRepository repository, IEmployeService employeService) {
        return new AgenceServiceImpl(repository, mock(IVehiculeRepository.class),
                mock(IVehiculeService.class), employeService, mock(RapportFactory.class));
    }
}

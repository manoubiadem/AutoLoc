package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IPaiementRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaiementServiceImplTest {

    private IPaiementRepository repository;
    private PaiementServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IPaiementRepository.class);
        service = new PaiementServiceImpl(repository);
    }

    @Test
    void findByIdRetournePaiementExistant() {
        Paiement paiement = mock(Paiement.class);
        when(repository.findById(1L)).thenReturn(Optional.of(paiement));
        assertSame(paiement, service.findById(1L));
    }

    @Test
    void findByIdSignalePaiementInexistant() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void findAllRetourneLesPaiements() {
        List<Paiement> paiements = List.of(mock(Paiement.class));
        when(repository.findAll()).thenReturn(paiements);
        assertSame(paiements, service.findAll());
    }

    @Test
    void findAllPeutRetournerListeVide() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(service.findAll().isEmpty());
    }
}

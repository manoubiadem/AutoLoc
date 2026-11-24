package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContratServiceImplTest {

    private IContratRepository repository;
    private ContratServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IContratRepository.class);
        service = new ContratServiceImpl(repository);
    }

    @Test
    void checkAccepteUneEntite() {
        assertTrue(service.check(mock(Contrat.class), true));
    }

    @Test
    void checkRefuseNull() {
        assertFalse(service.check(null, true));
    }

    @Test
    void createEnregistreEntiteValide() {
        Contrat entity = mock(Contrat.class);
        when(entity.getIdContrat()).thenReturn((Long) null);
        when(repository.save(entity)).thenReturn(entity);
        assertSame(entity, service.create(entity));
        verify(repository).save(entity);
    }

    @Test
    void createRefuseIdentifiantDejaRenseigne() {
        Contrat entity = mock(Contrat.class);
        when(entity.getIdContrat()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> service.create(entity));
        verify(repository, never()).save(any());
    }

    @Test
    void findByIdRetourneEntiteTrouvee() {
        Contrat entity = mock(Contrat.class);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        assertSame(entity, service.findById(1L));
    }

    @Test
    void findByIdSignaleEntiteAbsente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void findAllRetourneListeDuRepository() {
        List<Contrat> liste = List.of(mock(Contrat.class));
        when(repository.findAll()).thenReturn(liste);
        assertSame(liste, service.findAll());
    }

    @Test
    void updateCopieLesChampsEtEnregistre() {
        Contrat source = mock(Contrat.class);
        Contrat existing = mock(Contrat.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));
        verify(existing).setDateSignature(source.getDateSignature());
        verify(repository).save(existing);
    }

    @Test
    void updateRefuseUneSourceNulle() {
        when(repository.findById(1L)).thenReturn(Optional.of(mock(Contrat.class)));
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, null));
        verify(repository, never()).save(any());
    }

    @Test
    void deleteByIdSupprimeSiEntiteExiste() {
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteByIdSignaleEntiteAbsente() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(99L));
        verify(repository, never()).deleteById(any());
    }
}

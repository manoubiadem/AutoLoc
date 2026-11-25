package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeServiceImplTest {

    private IEmployeRepository repository;
    private EmployeServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IEmployeRepository.class);
        service = new EmployeServiceImpl(repository);
    }

    @Test
    void checkAccepteUneEntite() {
        assertTrue(service.check(mock(Employe.class), true));
    }

    @Test
    void checkRefuseNull() {
        assertFalse(service.check(null, true));
    }

    @Test
    void createEnregistreEntiteValide() {
        Employe entity = mock(Employe.class);
        when(entity.getIdEmploye()).thenReturn((Long) null);
        when(repository.save(entity)).thenReturn(entity);
        assertSame(entity, service.create(entity));
        verify(repository).save(entity);
    }

    @Test
    void createRefuseIdentifiantDejaRenseigne() {
        Employe entity = mock(Employe.class);
        when(entity.getIdEmploye()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> service.create(entity));
        verify(repository, never()).save(any());
    }

    @Test
    void findByIdRetourneEntiteTrouvee() {
        Employe entity = mock(Employe.class);
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
        List<Employe> liste = List.of(mock(Employe.class));
        when(repository.findAll()).thenReturn(liste);
        assertSame(liste, service.findAll());
    }

    @Test
    void updateCopieLesChampsEtEnregistre() {
        Employe source = mock(Employe.class);
        Employe existing = mock(Employe.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));
        verify(existing).setNom(source.getNom());
        verify(repository).save(existing);
    }

    @Test
    void updateRefuseUneSourceNulle() {
        when(repository.findById(1L)).thenReturn(Optional.of(mock(Employe.class)));
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, null));
        verify(repository, never()).save(any());
    }

    @Test
    void updatePreserveIdentifiantEtAgence() {
        Employe existing = new Employe();
        existing.setIdEmploye(1L);
        Agence agence = new Agence();
        existing.setAgence(agence);
        Employe source = new Employe();
        source.setIdEmploye(999L);
        source.setAgence(new Agence());
        source.setNom("Nom modifie");
        source.setRole("Responsable location");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));

        assertEquals("Nom modifie", existing.getNom());
        assertEquals("Responsable location", existing.getRole());
        assertEquals(1L, existing.getIdEmploye());
        assertSame(agence, existing.getAgence());
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

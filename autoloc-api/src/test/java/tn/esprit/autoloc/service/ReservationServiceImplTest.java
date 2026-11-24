package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    private IReservationRepository repository;
    private ReservationServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IReservationRepository.class);
        service = new ReservationServiceImpl(repository);
    }

    @Test
    void checkAccepteUneEntite() {
        assertTrue(service.check(mock(Reservation.class), true));
    }

    @Test
    void checkRefuseNull() {
        assertFalse(service.check(null, true));
    }

    @Test
    void createEnregistreEntiteValide() {
        Reservation entity = mock(Reservation.class);
        when(entity.getIdReservation()).thenReturn((Long) null);
        when(repository.save(entity)).thenReturn(entity);
        assertSame(entity, service.create(entity));
        verify(repository).save(entity);
    }

    @Test
    void createRefuseIdentifiantDejaRenseigne() {
        Reservation entity = mock(Reservation.class);
        when(entity.getIdReservation()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> service.create(entity));
        verify(repository, never()).save(any());
    }

    @Test
    void findByIdRetourneEntiteTrouvee() {
        Reservation entity = mock(Reservation.class);
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
        List<Reservation> liste = List.of(mock(Reservation.class));
        when(repository.findAll()).thenReturn(liste);
        assertSame(liste, service.findAll());
    }

    @Test
    void updateCopieLesChampsEtEnregistre() {
        Reservation source = mock(Reservation.class);
        Reservation existing = mock(Reservation.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));
        verify(existing).setDateDebut(source.getDateDebut());
        verify(repository).save(existing);
    }

    @Test
    void updateRefuseUneSourceNulle() {
        when(repository.findById(1L)).thenReturn(Optional.of(mock(Reservation.class)));
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

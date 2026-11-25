package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    private IClientRepository repository;
    private ClientServiceImpl service;

    @BeforeEach
    void preparer() {
        repository = mock(IClientRepository.class);
        service = new ClientServiceImpl(repository);
    }

    @Test
    void checkAccepteUneEntite() {
        assertTrue(service.check(mock(Client.class), true));
    }

    @Test
    void checkRefuseNull() {
        assertFalse(service.check(null, true));
    }

    @Test
    void createEnregistreEntiteValide() {
        Client entity = mock(Client.class);
        when(entity.getIdClient()).thenReturn((Long) null);
        when(repository.save(entity)).thenReturn(entity);
        assertSame(entity, service.create(entity));
        verify(repository).save(entity);
    }

    @Test
    void createRefuseIdentifiantDejaRenseigne() {
        Client entity = mock(Client.class);
        when(entity.getIdClient()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> service.create(entity));
        verify(repository, never()).save(any());
    }

    @Test
    void createFixeLaDateInscriptionCoteServeur() {
        Client client = new Client();
        client.setDateInscription(LocalDate.of(1990, 1, 1));
        LocalDate avant = LocalDate.now();
        when(repository.save(client)).thenReturn(client);

        assertSame(client, service.create(client));

        assertFalse(client.getDateInscription().isBefore(avant));
        assertFalse(client.getDateInscription().isAfter(LocalDate.now()));
        verify(repository).save(client);
    }

    @Test
    void updatePreserveIdentifiantDateInscriptionEtReservations() {
        Client existing = new Client();
        existing.setIdClient(1L);
        existing.setDateInscription(LocalDate.of(2020, 2, 3));
        List<Reservation> reservations = List.of(new Reservation());
        existing.setReservations(reservations);
        Client source = new Client();
        source.setIdClient(999L);
        source.setNom("Nom modifie");
        source.setDateInscription(LocalDate.of(1990, 1, 1));
        source.setReservations(List.of(new Reservation()));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));

        assertEquals("Nom modifie", existing.getNom());
        assertEquals(1L, existing.getIdClient());
        assertEquals(LocalDate.of(2020, 2, 3), existing.getDateInscription());
        assertSame(reservations, existing.getReservations());
    }

    @Test
    void findByIdRetourneEntiteTrouvee() {
        Client entity = mock(Client.class);
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
        List<Client> liste = List.of(mock(Client.class));
        when(repository.findAll()).thenReturn(liste);
        assertSame(liste, service.findAll());
    }

    @Test
    void updateCopieLesChampsEtEnregistre() {
        Client source = mock(Client.class);
        Client existing = mock(Client.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(1L, source));
        verify(existing).setNom(source.getNom());
        verify(repository).save(existing);
    }

    @Test
    void updateRefuseUneSourceNulle() {
        when(repository.findById(1L)).thenReturn(Optional.of(mock(Client.class)));
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

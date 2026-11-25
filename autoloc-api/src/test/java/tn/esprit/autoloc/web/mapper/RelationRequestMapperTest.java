package tn.esprit.autoloc.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.ReservationRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RelationRequestMapperTest {

    private IVehiculeService vehiculeService;
    private IClientService clientService;
    private IContratService contratService;
    private RelationRequestMapper mapper;
    private final LocalDate debut = LocalDate.of(2026, 11, 25);
    private final LocalDate fin = LocalDate.of(2026, 11, 28);

    @BeforeEach
    void preparer() {
        vehiculeService = mock(IVehiculeService.class);
        clientService = mock(IClientService.class);
        contratService = mock(IContratService.class);
        mapper = new RelationRequestMapper(vehiculeService, clientService, contratService);
    }

    @Test
    void maintenanceUtiliseLeVehiculeTrouveCoteServeur() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(11L);
        vehicule.setMarque("Marque conservee");
        when(vehiculeService.findById(11L)).thenReturn(vehicule);

        Maintenance entity = mapper.toEntity(new MaintenanceRequest(debut, fin, "Revision", 11L));

        assertNull(entity.getIdMaintenance());
        assertEquals(debut, entity.getDateDebut());
        assertEquals(fin, entity.getDateFin());
        assertEquals("Revision", entity.getDescription());
        assertSame(vehicule, entity.getVehicule());
        assertEquals("Marque conservee", vehicule.getMarque());
        verify(vehiculeService).findById(11L);
        verifyNoInteractions(clientService, contratService);
    }

    @Test
    void reservationResoutUniquementLesIdentifiantsAutorises() {
        Vehicule vehicule = new Vehicule();
        Client client = new Client();
        Contrat contrat = new Contrat();
        when(vehiculeService.findById(11L)).thenReturn(vehicule);
        when(clientService.findById(12L)).thenReturn(client);
        when(contratService.findById(13L)).thenReturn(contrat);

        Reservation entity = mapper.toEntity(new ReservationRequest(debut, fin, "CONFIRMEE", 11L, 12L, 13L));

        assertNull(entity.getIdReservation());
        assertEquals(debut, entity.getDateDebut());
        assertEquals(fin, entity.getDateFin());
        assertEquals("CONFIRMEE", entity.getStatut());
        assertSame(vehicule, entity.getVehicule());
        assertSame(client, entity.getClient());
        assertSame(contrat, entity.getContrat());
        verify(vehiculeService).findById(11L);
        verify(clientService).findById(12L);
        verify(contratService).findById(13L);
    }

    @Test
    void relationsFacultativesRestentNullesSansChargement() {
        Maintenance maintenance = mapper.toEntity(new MaintenanceRequest(debut, fin, "Revision", null));
        Reservation reservation = mapper.toEntity(new ReservationRequest(debut, fin, "BROUILLON", null, null, null));

        assertNull(maintenance.getVehicule());
        assertNull(reservation.getVehicule());
        assertNull(reservation.getClient());
        assertNull(reservation.getContrat());
        verifyNoInteractions(vehiculeService, clientService, contratService);
    }

    @Test
    void maintenanceRefuseUnVehiculeInexistant() {
        when(vehiculeService.findById(99L)).thenThrow(new ResourceNotFoundException("Vehicule", "99"));

        assertThrows(ResourceNotFoundException.class,
                () -> mapper.toEntity(new MaintenanceRequest(debut, fin, "Revision", 99L)));
        verifyNoInteractions(clientService, contratService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"vehicule", "client", "contrat"})
    void reservationRefuseChaqueTypeDeRelationInexistante(String relation) {
        Long vehiculeId = null;
        Long clientId = null;
        Long contratId = null;
        switch (relation) {
            case "vehicule" -> {
                vehiculeId = 99L;
                when(vehiculeService.findById(99L)).thenThrow(new ResourceNotFoundException("Vehicule", "99"));
            }
            case "client" -> {
                clientId = 99L;
                when(clientService.findById(99L)).thenThrow(new ResourceNotFoundException("Client", "99"));
            }
            case "contrat" -> {
                contratId = 99L;
                when(contratService.findById(99L)).thenThrow(new ResourceNotFoundException("Contrat", "99"));
            }
            default -> throw new IllegalArgumentException(relation);
        }
        ReservationRequest request = new ReservationRequest(debut, fin, "BROUILLON", vehiculeId, clientId, contratId);

        assertThrows(ResourceNotFoundException.class, () -> mapper.toEntity(request));
    }
}

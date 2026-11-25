package tn.esprit.autoloc.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.ReservationRequest;

/** Resolution explicite des seules associations autorisees par ces deux operations. */
@Component
@RequiredArgsConstructor
public class RelationRequestMapper {

    private final IVehiculeService vehiculeService;
    private final IClientService clientService;
    private final IContratService contratService;

    public Maintenance toEntity(MaintenanceRequest request) {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(request.dateDebut());
        maintenance.setDateFin(request.dateFin());
        maintenance.setDescription(request.description());
        if (request.vehiculeId() != null) {
            maintenance.setVehicule(vehiculeService.findById(request.vehiculeId()));
        }
        return maintenance;
    }

    public Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatut(request.statut());
        if (request.vehiculeId() != null) {
            reservation.setVehicule(vehiculeService.findById(request.vehiculeId()));
        }
        if (request.clientId() != null) {
            reservation.setClient(clientService.findById(request.clientId()));
        }
        if (request.contratId() != null) {
            reservation.setContrat(contratService.findById(request.contratId()));
        }
        return reservation;
    }
}

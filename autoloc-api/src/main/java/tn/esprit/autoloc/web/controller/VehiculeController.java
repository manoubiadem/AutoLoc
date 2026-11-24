
package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;



@Tag(name = "Vehicules", description = "Gestion des véhicules")
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Lister les véhicules")
    @GetMapping
    public List<Vehicule> findAll() {



        return vehiculeService.findAll();
    }

    @Operation(summary = "Consulter un véhicule")
    @GetMapping("/{id}")
    public Vehicule findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Ajouter un véhicule")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @Operation(summary = "Modifier un véhicule")
    @PutMapping("/{id}")
    public Vehicule update(
            @PathVariable Long id,
            @RequestBody Vehicule vehicule) {

        return vehiculeService.update(id, vehicule);
    }

    @Operation(summary = "Supprimer un véhicule")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @Operation(summary = "Désaffecter une agence d'un véhicule")
    @PutMapping("/{id}/desaffecter-agence")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desaffecterAgence(@PathVariable Long id) {
        vehiculeService.desaffecterAgence(id);
    }

    @Operation(summary = "Affecter une agence à un véhicule")
    @PutMapping("/{idVehicule}/affecter-agence/{idAgence}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterAgence(
            @PathVariable Long idVehicule,
            @PathVariable Long idAgence) {

        vehiculeService.affecterAgence(idVehicule, idAgence);
    }

    @PutMapping("/{idVehicule}/affecter-equipement/{idEquipement}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterEquipement(
            @PathVariable Long idVehicule,
            @PathVariable Long idEquipement) {

        vehiculeService.affecterEquipement(
                idVehicule,
                idEquipement
        );
    }
}

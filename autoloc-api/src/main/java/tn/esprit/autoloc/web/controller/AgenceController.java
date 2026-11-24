
package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.ISimulationAgenceService;

import java.util.List;

@Tag(
        name = "Agences",
        description = "Gestion des agences de location"
)
@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;
    private final ISimulationAgenceService simulationAgenceService;

    @Operation(summary = "Lister les agences")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des agences"
    )
    @GetMapping
    public List<Agence> findAll() {
        return agenceService.findAll();
    }

    @Operation(summary = "Consulter une agence")
    @ApiResponse(
            responseCode = "200",
            description = "Agence trouvée"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Agence introuvable"
    )
    @GetMapping("/{id}")
    public Agence findById(
            @Parameter(description = "Identifiant")
            @PathVariable Long id) {

        return agenceService.findById(id);
    }

    @Operation(summary = "Ajouter une agence")
    @ApiResponse(
            responseCode = "201",
            description = "Agence ajoutée"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Erreur lors de l'ajout"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agence create(
            @RequestBody Agence agence) {

        return agenceService.create(agence);
    }

    @Operation(summary = "Modifier une agence")
    @ApiResponse(
            responseCode = "200",
            description = "Agence modifiée"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Agence introuvable"
    )
    @PutMapping("/{id}")
    public Agence update(
            @Parameter(description = "Identifiant")
            @PathVariable Long id,
            @RequestBody Agence agence) {

        return agenceService.update(id, agence);
    }

    @Operation(summary = "Supprimer une agence")
    @ApiResponse(
            responseCode = "204",
            description = "Agence supprimée"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Agence introuvable"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description = "Identifiant")
            @PathVariable Long id) {

        agenceService.deleteById(id);
    }

    @Operation(summary = "Simuler le tarif d'une agence")
    @GetMapping("/{id}/simuler")
    public String simuler(
            @PathVariable Long id,
            @RequestParam int nbJours,
            @RequestParam VehiculeOffre offre) {

        Agence agence = agenceService.findById(id);

        return simulationAgenceService.simuler(
                agence,
                nbJours,
                offre
        );
    }

    // Atelier 9 - Question 13
    // Modifier une agence avec ses employes

    @Operation(summary = "Modifier une agence avec ses employes")
    @PutMapping("/{id}/avec-employes")
    public Agence modifierAgenceAvecEmployes(
            @PathVariable Long id,
            @RequestBody Agence agence) {

        return agenceService.modifierAgenceAvecEmployes(
                id,
                agence
        );
    }
}

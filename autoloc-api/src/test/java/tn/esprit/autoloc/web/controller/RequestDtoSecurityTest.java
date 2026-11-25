package tn.esprit.autoloc.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.service.*;
import tn.esprit.autoloc.web.GlobalExceptionHandler;
import tn.esprit.autoloc.web.mapper.RelationRequestMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequestDtoSecurityTest {

    // Both HTTP operations must enforce the same allowlist. PUT must use the path ID.
    private <T> T submit(Object controller, IGenericService<T, Long> service,
                         Class<T> entityType, String path, String json, boolean update) throws Exception {
        MockMvc mvc = mvc(controller);
        mvc.perform((update ? put(path + "/7") : post(path))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(update ? 200 : 201));

        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(entityType);
        if (update) {
            verify(service).update(eq(7L), captor.capture());
        } else {
            verify(service).create(captor.capture());
        }
        return captor.getValue();
    }

    private MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void agenceAccepteLesCoordonneesSansIdentifiantEffectifOuRelations(boolean update) throws Exception {
        IAgenceService service = mock(IAgenceService.class);
        Agence entity = submit(new AgenceController(service, mock(ISimulationAgenceService.class)),
                service, Agence.class, "/agences", """
                {"idAgence":999,"nom":"Centre","ville":"Tunis","adresse":"Rue 1","telephone":"1234",
                 "effectif":1000,"vehicules":[{"idVehicule":999}],"employes":[{"idEmploye":999}]}
                """, update);

        assertAll(() -> assertNull(entity.getIdAgence()),
                () -> assertEquals("Centre", entity.getNom()),
                () -> assertEquals("Tunis", entity.getVille()),
                () -> assertEquals("Rue 1", entity.getAdresse()),
                () -> assertEquals("1234", entity.getTelephone()),
                () -> assertEquals(0, entity.getEffectif()),
                () -> assertNull(entity.getVehicules()),
                () -> assertNull(entity.getEmployes()));
    }

    @Test
    void agenceAvecEmployesCreeUniquementDeNouveauxEmployesSansAgenceInjectee() throws Exception {
        IAgenceService service = mock(IAgenceService.class);
        mvc(new AgenceController(service, mock(ISimulationAgenceService.class)))
                .perform(put("/agences/7/avec-employes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"idAgence":999,"nom":"Centre","ville":"Tunis","adresse":"Rue 1","telephone":"1234",
                         "effectif":1000,"vehicules":[{"idVehicule":999}],
                         "employes":[{"idEmploye":999,"nom":"Ali","prenom":"Sami","role":"Agent",
                                      "agence":{"idAgence":999,"nom":"Autre agence"}}]}
                        """))
                .andExpect(status().isOk());

        ArgumentCaptor<Agence> captor = ArgumentCaptor.forClass(Agence.class);
        verify(service).modifierAgenceAvecEmployes(eq(7L), captor.capture());
        Agence entity = captor.getValue();
        assertAll(() -> assertNull(entity.getIdAgence()),
                () -> assertEquals("Centre", entity.getNom()),
                () -> assertEquals("Tunis", entity.getVille()),
                () -> assertEquals("Rue 1", entity.getAdresse()),
                () -> assertEquals("1234", entity.getTelephone()),
                () -> assertEquals(0, entity.getEffectif()),
                () -> assertNull(entity.getVehicules()),
                () -> assertEquals(1, entity.getEmployes().size()));
        Employe employe = entity.getEmployes().get(0);
        assertAll(() -> assertNull(employe.getIdEmploye()),
                () -> assertEquals("Ali", employe.getNom()),
                () -> assertEquals("Sami", employe.getPrenom()),
                () -> assertEquals("Agent", employe.getRole()),
                () -> assertNull(employe.getAgence()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void clientNePeutPasFournirSonIdentifiantSaDateInscriptionOuSesReservations(boolean update) throws Exception {
        IClientService service = mock(IClientService.class);
        Client entity = submit(new ClientController(service), service, Client.class, "/clients", """
                {"idClient":999,"nom":"Ali","prenom":"Sami","email":"ali@example.com",
                 "telephone":"1234","numPermis":"P123","dateInscription":"1999-01-01",
                 "reservations":[{"idReservation":999}]}
                """, update);

        assertAll(() -> assertNull(entity.getIdClient()),
                () -> assertEquals("Ali", entity.getNom()),
                () -> assertEquals("Sami", entity.getPrenom()),
                () -> assertEquals("ali@example.com", entity.getEmail()),
                () -> assertEquals("1234", entity.getTelephone()),
                () -> assertEquals("P123", entity.getNumPermis()),
                () -> assertNull(entity.getDateInscription()),
                () -> assertNull(entity.getReservations()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void contratAccepteLesChampsMetierSansPaiementsOuReservationInjectes(boolean update) throws Exception {
        IContratService service = mock(IContratService.class);
        Contrat entity = submit(new ContratController(service), service, Contrat.class, "/contrats", """
                {"idContrat":999,"dateSignature":"2026-11-01","montantTotal":300.50,"valide":true,
                 "paiements":[{"idPaiement":999,"montant":0}],"reservation":{"idReservation":999}}
                """, update);

        assertAll(() -> assertNull(entity.getIdContrat()),
                () -> assertEquals(LocalDate.of(2026, 11, 1), entity.getDateSignature()),
                () -> assertEquals(new BigDecimal("300.50"), entity.getMontantTotal()),
                () -> assertTrue(entity.isValide()),
                () -> assertNull(entity.getPaiements()),
                () -> assertNull(entity.getReservation()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void employeAccepteLesInformationsMetierSansAffectationAgence(boolean update) throws Exception {
        IEmployeService service = mock(IEmployeService.class);
        Employe entity = submit(new EmployeController(service), service, Employe.class, "/employes", """
                {"idEmploye":999,"nom":"Ali","prenom":"Sami","role":"Agent",
                 "agence":{"idAgence":999,"nom":"Autre agence"}}
                """, update);

        assertAll(() -> assertNull(entity.getIdEmploye()),
                () -> assertEquals("Ali", entity.getNom()),
                () -> assertEquals("Sami", entity.getPrenom()),
                () -> assertEquals("Agent", entity.getRole()),
                () -> assertNull(entity.getAgence()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void equipementAccepteUniquementSonLibelle(boolean update) throws Exception {
        IEquipementService service = mock(IEquipementService.class);
        Equipement entity = submit(new EquipementController(service), service, Equipement.class,
                "/equipements", """
                {"idEquipement":999,"libelle":"GPS","vehicules":[{"idVehicule":999}]}
                """, update);

        assertAll(() -> assertNull(entity.getIdEquipement()),
                () -> assertEquals("GPS", entity.getLibelle()),
                () -> assertNull(entity.getVehicules()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void vehiculeAccepteLesChampsMetierSansModifierSesRelations(boolean update) throws Exception {
        IVehiculeService service = mock(IVehiculeService.class);
        Vehicule entity = submit(new VehiculeController(service), service, Vehicule.class,
                "/vehicules", """
                {"idVehicule":999,"immatriculation":"123TU456","marque":"Toyota","modele":"Yaris",
                 "categorie":"CITADINE","tarifJournalier":80.50,"statut":"DISPONIBLE",
                 "agence":{"idAgence":999},"reservations":[{"idReservation":999}],
                 "equipements":[{"idEquipement":999}]}
                """, update);

        assertAll(() -> assertNull(entity.getIdVehicule()),
                () -> assertEquals("123TU456", entity.getImmatriculation()),
                () -> assertEquals("Toyota", entity.getMarque()),
                () -> assertEquals("Yaris", entity.getModele()),
                () -> assertEquals(CategorieVehicule.CITADINE, entity.getCategorie()),
                () -> assertEquals(new BigDecimal("80.50"), entity.getTarifJournalier()),
                () -> assertEquals(StatutVehicule.DISPONIBLE, entity.getStatut()),
                () -> assertNull(entity.getAgence()),
                () -> assertNull(entity.getReservations()),
                () -> assertNull(entity.getEquipements()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void maintenanceUtiliseUnVehiculeExistantSansLiaisonDObjetImbrique(boolean update) throws Exception {
        IMaintenanceService service = mock(IMaintenanceService.class);
        IVehiculeService vehiculeService = mock(IVehiculeService.class);
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(13L);
        vehicule.setMarque("Toyota");
        when(vehiculeService.findById(13L)).thenReturn(vehicule);
        RelationRequestMapper mapper = new RelationRequestMapper(vehiculeService,
                mock(IClientService.class), mock(IContratService.class));

        Maintenance entity = submit(new MaintenanceController(service, mapper), service,
                Maintenance.class, "/maintenances", """
                {"idMaintenance":999,"dateDebut":"2026-11-01","dateFin":"2026-11-03",
                 "description":"Vidange","vehiculeId":13,
                 "vehicule":{"idVehicule":999,"marque":"Modifiee"}}
                """, update);

        assertAll(() -> assertNull(entity.getIdMaintenance()),
                () -> assertEquals(LocalDate.of(2026, 11, 1), entity.getDateDebut()),
                () -> assertEquals(LocalDate.of(2026, 11, 3), entity.getDateFin()),
                () -> assertEquals("Vidange", entity.getDescription()),
                () -> assertSame(vehicule, entity.getVehicule()),
                () -> assertEquals("Toyota", vehicule.getMarque()));
        verify(vehiculeService).findById(13L);
        verifyNoMoreInteractions(vehiculeService);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void reservationResoutLesReferencesSansModifierLeurContenu(boolean update) throws Exception {
        IReservationService service = mock(IReservationService.class);
        IVehiculeService vehiculeService = mock(IVehiculeService.class);
        IClientService clientService = mock(IClientService.class);
        IContratService contratService = mock(IContratService.class);
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(13L);
        vehicule.setMarque("Toyota");
        Client client = new Client();
        client.setIdClient(17L);
        client.setNom("Client existant");
        Contrat contrat = new Contrat();
        contrat.setIdContrat(19L);
        contrat.setMontantTotal(new BigDecimal("300"));
        when(vehiculeService.findById(13L)).thenReturn(vehicule);
        when(clientService.findById(17L)).thenReturn(client);
        when(contratService.findById(19L)).thenReturn(contrat);
        RelationRequestMapper mapper = new RelationRequestMapper(vehiculeService, clientService, contratService);

        Reservation entity = submit(new ReservationController(service, mapper), service,
                Reservation.class, "/reservations", """
                {"idReservation":999,"dateDebut":"2026-11-01","dateFin":"2026-11-03","statut":"CONFIRMEE",
                 "vehiculeId":13,"clientId":17,"contratId":19,
                 "vehicule":{"idVehicule":999,"marque":"Modifiee"},
                 "client":{"idClient":999,"nom":"Modifie"},
                 "contrat":{"idContrat":999,"montantTotal":0,"paiements":[{"montant":0}]}}
                """, update);

        assertAll(() -> assertNull(entity.getIdReservation()),
                () -> assertEquals(LocalDate.of(2026, 11, 1), entity.getDateDebut()),
                () -> assertEquals(LocalDate.of(2026, 11, 3), entity.getDateFin()),
                () -> assertEquals("CONFIRMEE", entity.getStatut()),
                () -> assertSame(vehicule, entity.getVehicule()),
                () -> assertSame(client, entity.getClient()),
                () -> assertSame(contrat, entity.getContrat()),
                () -> assertEquals("Toyota", vehicule.getMarque()),
                () -> assertEquals("Client existant", client.getNom()),
                () -> assertEquals(new BigDecimal("300"), contrat.getMontantTotal()),
                () -> assertNull(contrat.getPaiements()));
        verify(vehiculeService).findById(13L);
        verify(clientService).findById(17L);
        verify(contratService).findById(19L);
        verifyNoMoreInteractions(vehiculeService, clientService, contratService);
    }

    @ParameterizedTest
    @CsvSource({"maintenances,vehicule", "reservations,vehicule", "reservations,client", "reservations,contrat"})
    void referenceInexistanteRetourne404SansEnregistrer(String path, String relation) throws Exception {
        IVehiculeService vehiculeService = mock(IVehiculeService.class);
        IClientService clientService = mock(IClientService.class);
        IContratService contratService = mock(IContratService.class);
        switch (relation) {
            case "vehicule" -> when(vehiculeService.findById(999L))
                    .thenThrow(new ResourceNotFoundException("Vehicule", "999"));
            case "client" -> when(clientService.findById(999L))
                    .thenThrow(new ResourceNotFoundException("Client", "999"));
            case "contrat" -> when(contratService.findById(999L))
                    .thenThrow(new ResourceNotFoundException("Contrat", "999"));
            default -> fail("Relation inattendue");
        }
        IMaintenanceService maintenanceService = mock(IMaintenanceService.class);
        IReservationService reservationService = mock(IReservationService.class);
        RelationRequestMapper mapper = new RelationRequestMapper(vehiculeService, clientService, contratService);
        Object controller = path.equals("maintenances")
                ? new MaintenanceController(maintenanceService, mapper)
                : new ReservationController(reservationService, mapper);

        mvc(controller).perform(post("/" + path).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + relation + "Id\":999}"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(maintenanceService, reservationService);
    }
}

package tn.esprit.autoloc.autolocapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    private void addAgence(CrudRepository<Agence, Long> repository) {

        int time = (int) System.currentTimeMillis();

        Agence agence = new Agence();
        agence.setNom("Agence ariana");
        agence.setAdresse("1 Rue Hedi");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        Vehicule vehicule1 = new Vehicule();
        vehicule1.setImmatriculation("TU96" + time);
        vehicule1.setMarque("Isuzu");
        vehicule1.setModele("DMax");
        vehicule1.setCategorie(CategorieVehicule.SUV);
        vehicule1.setStatut(StatutVehicule.MAINTENANCE);
        vehicule1.setTarifJournalier(new BigDecimal("100"));
        vehicule1.setAgence(agence);

        Vehicule vehicule2 = new Vehicule();
        vehicule2.setImmatriculation("TU95" + time);
        vehicule2.setMarque("Toyota");
        vehicule2.setModele("Yaris");
        vehicule2.setCategorie(CategorieVehicule.UTILITAIRE);
        vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setTarifJournalier(new BigDecimal("80"));
        vehicule2.setAgence(agence);

        agence.setVehicules(List.of(vehicule1, vehicule2));

        repository.save(agence);
    }
    @Test
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
    }

    @Test
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
    }


    private void loadAgence(CrudRepository<Agence, Long> repository, String type) {

        Iterable<Agence> agences = repository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Repository : ").append(type).append("\n");

        for (Agence agence : agences) {

            sb.append(agence.getIdAgence())
                    .append(" | ")
                    .append(agence.getNom())
                    .append("\n");

            sb.append("Vehicules Count : ")
                    .append(agence.getVehicules().size())
                    .append("\n");

            for (Vehicule vehicule : agence.getVehicules()) {
                sb.append("=== ")
                        .append(vehicule.getIdVehicule())
                        .append("|")
                        .append(vehicule.getImmatriculation())
                        .append("\n");
            }
        }

        fail(sb.toString());
    }
    @Test
    public void basicLoadAgence() {
        loadAgence(basicAgenceRepository, "Basic Repository");
    }

    @Test
    public void fullLoadAgence() {
        loadAgence(fullAgenceRepository, "Full Repository");
    }
    @Test
    public void loadSortedAgences() {

        List<Agence> agences =
                fullAgenceRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "idAgence")
                );

        StringBuilder sb = new StringBuilder();

        for (Agence agence : agences) {
            sb.append(agence.getIdAgence())
                    .append(" | ")
                    .append(agence.getNom())
                    .append("\n");
        }

        System.out.println(sb.toString());
    }
    @Test
    public void loadPagedAgences() {

        Page<Agence> agences =
                fullAgenceRepository.findAll(
                        PageRequest.of(
                                0,
                                2,
                                Sort.by(Sort.Direction.DESC, "idAgence")
                        )
                );
        StringBuilder sb = new StringBuilder();

        sb.append("Nombre total de pages : ")
                .append(agences.getTotalPages())
                .append("\n");

        sb.append("Page en cours : ")
                .append(agences.getNumber())
                .append("\n");

        for (Agence agence : agences) {
            sb.append(agence.getIdAgence())
                    .append(" | ")
                    .append(agence.getNom())
                    .append("\n");
        }

        System.out.println(sb.toString());
    }
}

interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
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
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock agenceRepository;

    @Test
    public void addAgence() {

        Agence agence = new Agence();
        agence.setNom("Agence ariana");
        agence.setAdresse("1 Rue Hedi");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        Vehicule vehicule1 = new Vehicule();
        vehicule1.setImmatriculation("785414TU96");
        vehicule1.setMarque("Isuzu");
        vehicule1.setModele("DMax");
        vehicule1.setCategorie(CategorieVehicule.SUV);
        vehicule1.setStatut(StatutVehicule.MAINTENANCE);
        vehicule1.setTarifJournalier(new BigDecimal("100"));
        vehicule1.setAgence(agence);

        Vehicule vehicule2 = new Vehicule();
        vehicule2.setImmatriculation("785414TU95");
        vehicule2.setMarque("Toyota");
        vehicule2.setModele("Yaris");
        vehicule2.setCategorie(CategorieVehicule.UTILITAIRE);
        vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setTarifJournalier(new BigDecimal("80"));
        vehicule2.setAgence(agence);

        agence.setVehicules(List.of(vehicule1, vehicule2));
        agenceRepository.save(agence);
    }
    @Test
    public void loadAgence() {

        Iterable<Agence> agences = agenceRepository.findAll();

        StringBuilder sb = new StringBuilder();

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
}

interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
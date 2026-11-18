
package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;

    private String ville;

    private String adresse;

    private String telephone;

    @Column(name = "effectif")
    private int effectif;

    @OneToMany(
            mappedBy = "agence",
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference("agence-vehicules")
    private List<Vehicule> vehicules;

    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private List<Employe> employes;

    // Ajouter un employe a une agence
    public void ajouterEmploye(Employe employe) {

        if (employe == null) {
            return;
        }

        if (this.employes == null) {
            this.employes = new ArrayList<>();
        }

        Agence ancienneAgence = employe.getAgence();

        if (ancienneAgence != null && ancienneAgence != this) {
            ancienneAgence.retirerEmploye(employe);
        }

        if (!this.employes.contains(employe)) {
            this.employes.add(employe);
        }

        employe.setAgence(this);
    }

    // Retirer un employe d'une agence
    public void retirerEmploye(Employe employe) {

        if (employe == null) {
            return;
        }

        if (this.employes != null) {
            this.employes.remove(employe);
        }

        if (employe.getAgence() == this) {
            employe.setAgence(null);
        }
    }
}

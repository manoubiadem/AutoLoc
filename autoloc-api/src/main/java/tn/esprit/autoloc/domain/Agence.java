package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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

    @OneToMany(
            mappedBy = "agence",
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER
    )
    private List<Vehicule> vehicules;
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private List<Employe> employes;
}
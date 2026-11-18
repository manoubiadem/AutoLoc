
package tn.esprit.autoloc.domain;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "equipement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    private String libelle;

    @ManyToMany(
            mappedBy = "equipements",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Vehicule> vehicules;
}

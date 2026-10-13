package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String numPermis;

    private LocalDate dateInscription;
    @OneToMany(
            mappedBy = "client",
            fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE
    )
    private List<Reservation> reservations;
}
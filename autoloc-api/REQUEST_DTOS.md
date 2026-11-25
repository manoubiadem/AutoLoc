# Corps des requetes REST

Les URL, methodes HTTP et reponses sont conservees. Les 17 entrees de creation
et modification utilisent les DTO de `tn.esprit.autoloc.web.dto`. Les conversions
copient explicitement les seuls champs ci-dessous, sans copie automatique de proprietes.

| Ressource | Champs acceptes dans POST et PUT |
| --- | --- |
| Agence | `nom`, `ville`, `adresse`, `telephone` |
| Client | `nom`, `prenom`, `email`, `telephone`, `numPermis` |
| Contrat | `dateSignature`, `montantTotal`, `valide` |
| Employe | `nom`, `prenom`, `role` |
| Equipement | `libelle` |
| Maintenance | `dateDebut`, `dateFin`, `description`, `vehiculeId` |
| Reservation | `dateDebut`, `dateFin`, `statut`, `vehiculeId`, `clientId`, `contratId` |
| Vehicule | `immatriculation`, `marque`, `modele`, `categorie`, `tarifJournalier`, `statut` |

Les champs inconnus sont ignores, y compris les identifiants des entites, les
objets JPA imbriques et les collections. L'identifiant de l'entite modifiee
provient uniquement de l'URL. `dateInscription` est attribuee par le serveur a
la creation du client et conservee a la modification. L'effectif de l'agence
reste gere par le service.

`role` est ici la fonction metier de l'employe, sans usage d'authentification
dans le projet actuel. Les montants, dates de signature et statuts restent des
champs metier editables : aucun calcul ou workflow serveur ne les gere actuellement.

Les references `vehiculeId`, `clientId`, `contratId` sont les seules associations
explicitement editables dans ces deux DTO. Elles sont chargees via les services
existants ; une reference inexistante produit une reponse 404. Une reference
absente ou nulle conserve la semantique de remplacement du PUT : l'association
correspondante devient nulle. Les donnees de l'objet reference ne sont jamais
copiees depuis la requete. Une maintenance ne cree plus de vehicule en cascade.

Exemple de reservation :

```json
{
  "dateDebut": "2026-12-01",
  "dateFin": "2026-12-03",
  "statut": "CONFIRMEE",
  "vehiculeId": 11,
  "clientId": 12,
  "contratId": 13
}
```

Les anciennes formes imbriquees (`vehicule`, `client`, `contrat`) doivent etre
remplacees par ces references. Les associations agence/equipements d'un vehicule
restent gerees par les endpoints d'affectation existants ; les mises a jour
ordinaires preservent ces associations et les collections protegees.

`PUT /agences/{id}/avec-employes` accepte les champs de l'agence et `employes`,
une liste de nouveaux employes avec seulement `nom`, `prenom`, `role`. Un
`idEmploye` ou une `agence` imbriquee est ignore : chaque employe est nouveau
et affecte cote serveur a l'agence de l'URL. L'effectif est ensuite recalcule.

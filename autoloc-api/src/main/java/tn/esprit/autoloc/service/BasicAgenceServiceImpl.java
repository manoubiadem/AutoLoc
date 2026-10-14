package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.util.List;
import tn.esprit.autoloc.Utils;

@Service
@RequiredArgsConstructor
public class BasicAgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    public boolean check(Agence agence, boolean isNew) {

        return agence != null
                && Utils.isNotEmpty(agence.getNom())
                && Utils.isNotEmpty(agence.getVille())
                && Utils.isNotEmpty(agence.getAdresse())
                && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    public Agence create(Agence agence) {

        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("L'identifiant doit être vide");
        }

        if (!check(agence, true)) {
            throw new IllegalArgumentException("Agence non valide");
        }

        return agenceRepository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agence", String.valueOf(id))
                );
    }

    @Override
    public List<Agence> findAll() {
        return null;
    }

    @Override
    public Agence update(Long id, Agence agence) {

        Agence existingAgence = findById(id);

        if (!check(agence, false)) {
            throw new IllegalArgumentException("Agence non valide");
        }

        existingAgence.setNom(agence.getNom());
        existingAgence.setVille(agence.getVille());
        existingAgence.setAdresse(agence.getAdresse());
        existingAgence.setTelephone(agence.getTelephone());
        existingAgence.setVehicules(agence.getVehicules());

        return agenceRepository.save(existingAgence);
    }

    @Override
    public void deleteById(Long id) {

        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", String.valueOf(id));
        }

        agenceRepository.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        return false;
    }
}
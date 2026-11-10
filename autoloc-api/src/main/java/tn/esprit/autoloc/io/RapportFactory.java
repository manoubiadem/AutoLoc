package tn.esprit.autoloc.io;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RapportFactory {

    private final RapportConsoleDecorator rapportConsoleDecorator;

    public Rapport creerRapport() {
        return rapportConsoleDecorator;
    }
}
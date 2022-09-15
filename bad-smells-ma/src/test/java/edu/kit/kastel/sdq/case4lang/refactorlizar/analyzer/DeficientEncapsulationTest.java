package edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer;

import encapsulation.deficient_encapsulation.DeficientEncapsulation;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DeficientEncapsulationTest {
    @Test
    void testFullAnalysis() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/KAMP/KAMP_ANALYSE");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/KAMP/KAMP_Sprache");
        Project project =
                new ProjectParser()
                        .setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                        .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .parse();
        new DeficientEncapsulation()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new DeficientEncapsulation().getSettings());
    }
}

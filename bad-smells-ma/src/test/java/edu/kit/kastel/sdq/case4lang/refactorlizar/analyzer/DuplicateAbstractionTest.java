package edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import abstraction.duplicated_abstraction.GraphIsomorphism;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;

public class DuplicateAbstractionTest {
    @Test
    void testFullAnalysis() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/smartGrid/Smart-Grid-ICT-Resilience-Framework_ANALYSE");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/smartGrid/Smart-Grid-ICT-Resilience-Framework_SPRACHE");
        Project project =
                new ProjectParser()
                        .setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                        .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .ignoreTestFolder(true)
                                        .parse();
        GraphIsomorphism isomorphism = new GraphIsomorphism();
        isomorphism.start(project, 2);
    }
}

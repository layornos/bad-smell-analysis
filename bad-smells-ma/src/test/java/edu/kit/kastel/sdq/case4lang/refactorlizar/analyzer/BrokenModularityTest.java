package edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer;

import modularity.broken_modularity.BrokenModularity;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BrokenModularityTest {
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
        new BrokenModularity()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new BrokenModularity().getSettings());
    }
}

package edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import modularity.weakened_modularity.DependencyCycle;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WeakenedModularityTest {
    @Test
    void testFullAnalysis() throws IOException {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "/Users/layornos/workspaces/diss/bad-smells/eval/LayeredAnalysisExample");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "/Users/layornos/workspaces/diss/bad-smells/eval_lang");
        Project project =
                new ProjectParser()
                        .setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                        .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .ignoreTestFolder(true)
                        .parse();
        Report report = new DependencyCycle().analyze(project.getLanguage(),
                project.getSimulatorModel(), new DependencyCycle().getSettings());
        Files.writeString(Path.of("weakened_modularity"), report.toString());
    }
}

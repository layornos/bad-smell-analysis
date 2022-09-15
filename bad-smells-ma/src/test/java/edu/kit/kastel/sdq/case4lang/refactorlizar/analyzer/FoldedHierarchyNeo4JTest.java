package edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer;

import hierarchy.folded_hierarchy.FoldedHierarchyNeo4j;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FoldedHierarchyNeo4JTest {
    @Test
    void testFullAnalysis() {
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
        FoldedHierarchyNeo4j foldedHierarchyNeo4j = new FoldedHierarchyNeo4j();
        foldedHierarchyNeo4j.start(project);
    }
}

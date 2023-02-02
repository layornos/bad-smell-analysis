package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import abstraction.duplicated_abstraction.DuplicateAbstraction;
import abstraction.missing_abstraction.MissingAbstraction;
import abstraction.unused_abstraction.SpeculativeGenerality;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import encapsulation.deficient_encapsulation.DeficientEncapsulation;
import hierarchy.folded_hierarchy.FoldedHierarchy;
import hierarchy.missing_hierarchy.MissingHierarchy;
import hierarchy.unexploited_Hierarchy.UnexploitedHierarchy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import modularity.broken_modularity.BrokenModularity;
import modularity.degraded_modularity.DegradedModularity;
import modularity.missing_modularity.MissingModularity;
import modularity.rebellious_modularity.LanguageBlob;
import modularity.weakened_modularity.DependencyCycle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class KampEval {

    private static Project project;

    @BeforeAll
    public static void setUp() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/KAMP_ANALYSE");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/KAMP_Sprache");
        project =
                new ProjectParser()
                        .setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                        .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .parse();
    }

    @Test
    public void broken_modularityReport() throws IOException {
        Report report =
                new BrokenModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        Files.writeString(Path.of("broken_modularity_kamp"), report.toString());
    }

    @Test
    public void deficient_encapsulationReport() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        Files.writeString(Path.of("deficient_encapsulation_kamp"), report.toString());
    }

    @Test
    public void degraded_modularityReport() throws IOException {
        Report report =
                new DegradedModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DegradedModularity().getSettings());
        Files.writeString(Path.of("degraded_modularity_kamp"), report.toString());
    }

    @Test
    public void duplicated_abstractionReport() throws IOException {
        Report report =
                new DuplicateAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DuplicateAbstraction().getSettings());
        Files.writeString(Path.of("CAMUNDA_duplicated_abstraction_kamp"), report.toString());
    }

    @Test
    public void incomplete_abstractionReport() throws IOException {
        Report report =
                new MissingHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingHierarchy().getSettings());
        Files.writeString(Path.of("incomplete_abstraction_kamp"), report.toString());
    }

    @Test
    public void missing_abstractionReport() throws IOException {
        Report report =
                new MissingAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingAbstraction().getSettings());
        Files.writeString(Path.of("missing_abstraction_kamp"), report.toString());
    }

    @Test
    public void missing_modularityReport() throws IOException {
        Report report =
                new MissingModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingModularity().getSettings());
        Files.writeString(Path.of("missing_modularity_kamp"), report.toString());
    }

    @Test
    public void rebellious_modularityReport() throws IOException {
        Report report =
                new LanguageBlob()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new LanguageBlob().getSettings());
        Files.writeString(Path.of("rebellious_modularity_kamp"), report.toString());
    }

    @Test
    public void unexploited_HierarchyReport() throws IOException {
        Report report =
                new UnexploitedHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnexploitedHierarchy().getSettings());
        Files.writeString(Path.of("unexploited_Hierarchy_kamp"), report.toString());
    }

    @Test
    public void unused_abstractionReport() throws IOException {
        Report report =
                new SpeculativeGenerality()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new SpeculativeGenerality().getSettings());
        Files.writeString(Path.of("unused_Abstraction_kamp"), report.toString());
    }

    @Test
    public void weakened_modularityReport() throws IOException {
        Report report =
                new DependencyCycle()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DependencyCycle().getSettings());
        Files.writeString(Path.of("weakened_modularity_kamp"), report.toString());
    }

    @Test
    public void folded_hierarchyReport() throws IOException {
        var setting = new FoldedHierarchy().getSettings();
        setting.setValue("layer", "domain,paradigm");
        Report report =
                new FoldedHierarchy()
                        .analyze(project.getLanguage(), project.getSimulatorModel(), setting);
        Files.writeString(Path.of("folded_hierarchy_kamp"), report.toString());
    }
}

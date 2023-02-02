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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import modularity.broken_modularity.BrokenModularity;
import modularity.degraded_modularity.DegradedModularity;
import modularity.missing_modularity.MissingModularity;
import modularity.rebellious_modularity.LanguageBlob;
import modularity.weakened_modularity.DependencyCycle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mosim.refactorlizar.architecture.evaluation.Application;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.Result;

public class KampEvalScenarios {

    private static Project project;
    private static String subPath =
            "Speculative Generality/47d3cca554f7f93ce70e044b0b9ad5d58d7488dc/before";

    @BeforeAll
    public static void setUp() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/historicalScenarios/"
                        + subPath);
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
        System.out.println(project.getLanguage().getComponents().size());
    }

    @Test
    public void broken_modularityReport() throws IOException {
        Report report =
                new BrokenModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        Files.writeString(Path.of("broken_modularity_simulizar"), report.toString());
    }

    @Test
    void deficient_encapsulationReport() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        System.out.println(report.toString());
    }

    @Test
    public void degraded_modularityReport() throws IOException {
        Report report =
                new DegradedModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DegradedModularity().getSettings());
        System.out.println(report.toString());
    }

    @Test
    public void duplicated_abstractionReport() throws IOException {
        Report report =
                new DuplicateAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DuplicateAbstraction().getSettings());
        Files.writeString(Path.of("duplicated_abstraction_simulizar"), report.toString());
    }

    @Test
    public void incomplete_abstractionReport() throws IOException {
        Report report =
                new MissingHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingHierarchy().getSettings());
        Files.writeString(Path.of("incomplete_abstraction_simulizar"), report.toString());
    }

    @Test
    public void missing_abstractionReport() throws IOException {
        Report report =
                new MissingAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingAbstraction().getSettings());
        System.out.println(report.toString());
    }

    @Test
    public void missing_modularityReport() throws IOException {
        Report report =
                new MissingModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingModularity().getSettings());
        Files.writeString(Path.of("missing_modularity_simulizar"), report.toString());
    }

    @Test
    public void rebellious_modularityReport() throws IOException {
        Report report =
                new LanguageBlob()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new LanguageBlob().getSettings());
        System.out.println(report.toString());
    }

    @Test
    public void unexploited_HierarchyReport() throws IOException {
        Report report =
                new UnexploitedHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnexploitedHierarchy().getSettings());
        Files.writeString(Path.of("unexploited_Hierarchy_simulizar"), report.toString());
    }

    @Test
    public void unused_abstractionReport() throws IOException {
        Report report =
                new SpeculativeGenerality()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new SpeculativeGenerality().getSettings());
        System.out.println(report.toString());
    }

    @Test
    public void weakened_modularityReport() throws IOException {
        Report report =
                new DependencyCycle()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DependencyCycle().getSettings());
        Files.writeString(Path.of("weakened_modularity_simulizar"), report.toString());
    }

    @Test
    public void folded_hierarchyReport() throws IOException {
        var setting = new FoldedHierarchy().getSettings();
        setting.setValue("layer", "domain,paradigm,analysis");
        Report report =
                new FoldedHierarchy()
                        .analyze(project.getLanguage(), project.getSimulatorModel(), setting);
        Files.writeString(Path.of("folded_hierarchy_simulizar"), report.toString());
    }

    // startTable("Simulizar", "Simulizar");

    void calculateMetric() {
        // Application application = new Application();
        // Result result = application.evaluate(CalculationMode.ONE_OFFSET,
        //                 "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni
        // zeugs/Masterarbeit/Projekte/eval/simulizar/fixedBadSmells/deficient_encapsulation");
        DecimalFormat df = new DecimalFormat("#.##");
        // System.out.println("Complexity: " + df.format(result.getComplexity().getValue()));
        // System.out.println("Cohesion: " + result.getCohesion().getValue());
        // System.out.println("Coupling: " + df.format(result.getCoupling().getValue()));
        // System.out.println("-----------------------------------------------------");
        // Complexity: 6035,21
        // Cohesion: -6.208100168616124E-6
        // Coupling: 1986,2
        Application application = new Application();
        Result result =
                application.evaluate(
                        CalculationMode.ONE_OFFSET,
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/Palladio-Analyzer-SimuLizar");
        System.out.println("Complexity: " + df.format(result.getComplexity().getValue()));
        System.out.println("Cohesion: " + result.getCohesion().getValue());
        System.out.println("Coupling: " + df.format(result.getCoupling().getValue()));
    }
}

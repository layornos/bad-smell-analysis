package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import abstraction.duplicated_abstraction.DuplicateAbstraction;
import abstraction.duplicated_abstraction.DuplicateAbstractionNeo4j;
import abstraction.missing_abstraction.MissingAbstraction;
import abstraction.unused_abstraction.SpeculativeGenerality;
import abstraction.unused_abstraction.UnusedAbstraction;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SmartGrid {

    private Project project;

    @BeforeEach
    public void setUp() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/smartGrid/Smart-Grid-ICT-Resilience-Framework_ANALYSE");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/smartGrid/Smart-Grid-ICT-Resilience-Framework_SPRACHE");
        project =
                new ProjectParser()
                        .setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                        .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .parse();
    }

    /*
     * ABSTRACTION
     */
    @Test
    public void duplicated_abstractionReport() throws IOException {
        Settings settings = new DuplicateAbstractionNeo4j().getSettings();
        settings.setValue("threshold", "3");
        settings.setValue("neo4j_uri", "bolt://localhost:7687");
        settings.setValue("neo4j_username", "neo4j");
        settings.setValue("neo4j_password", "test");
        Report report =
                new DuplicateAbstractionNeo4j()
                        .analyze(project.getLanguage(), project.getSimulatorModel(), settings);
        Files.writeString(Path.of("01_duplicated_abstraction_smartgrid"), report.toString());
    }

    @Test
    public void missing_abstractionReport() throws IOException {
        Report report =
                new MissingAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingAbstraction().getSettings());
        Files.writeString(Path.of("02_missing_abstraction_smartgrid"), report.toString());
    }

    @Test
    public void unused_abstractionReport() throws IOException {
        Settings settings = new UnusedAbstraction().getSettings();
        settings.setValue("neo4j_uri", "bolt://localhost:7687");
        settings.setValue("neo4j_username", "neo4j");
        settings.setValue("neo4j_password", "test");
        Report report =
                new UnusedAbstraction()
                        .analyze(project.getLanguage(), project.getSimulatorModel(), settings);
        Files.writeString(Path.of("03_unused_abstraction_smartgrid"), report.toString());
    }

    /*
     * ENCAPSULATION
     */
    @Test
    public void deficient_encapsulationReport() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        Files.writeString(Path.of("04_deficient_encapsulation_smartgrid"), report.toString());
    }

    /*
     * HIERARCHY
     */
    @Test
    public void folded_hierarchyReport() throws IOException {
        var setting = new FoldedHierarchy().getSettings();
        setting.setValue("layer", "paradigm,domain,quality,analysis");
        Report report =
                new FoldedHierarchy()
                        .analyze(project.getLanguage(), project.getSimulatorModel(), setting);
        Files.writeString(Path.of("05_folded_hierarchy_smartgrid"), report.toString());
    }

    @Test
    public void missing_HierarchyReport() throws IOException {
        Report report =
                new MissingHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingHierarchy().getSettings());
        Files.writeString(Path.of("06_missing_hierarchy_smartgrid"), report.toString());
    }

    @Test
    public void unexploited_HierarchyReport() throws IOException {
        Report report =
                new UnexploitedHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnexploitedHierarchy().getSettings());
        Files.writeString(Path.of("07_unexploited_hierarchy_smartgrid"), report.toString());
    }

    /*
     * MODULARITY
     */
    @Test
    public void broken_modularityReport() throws IOException {
        Report report =
                new BrokenModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        Files.writeString(Path.of("08_broken_modularity_smartgrid"), report.toString());
    }

    @Test
    public void degraded_modularityReport() throws IOException {
        Report report =
                new DegradedModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DegradedModularity().getSettings());
        Files.writeString(Path.of("09_degraded_modularity_smartgrid"), report.toString());
    }

    @Test
    public void missing_modularityReport() throws IOException {
        Report report =
                new MissingModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingModularity().getSettings());
        Files.writeString(Path.of("10_missing_modularity_smartgrid"), report.toString());
    }

    @Test
    public void rebellious_modularityReport() throws IOException {
        Report report =
                new LanguageBlob()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new LanguageBlob().getSettings());
        Files.writeString(Path.of("11_rebellious_modularity_smartgrid"), report.toString());
    }

    @Test
    public void weakened_modularityReport() throws IOException {
        Report report =
                new DependencyCycle()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DependencyCycle().getSettings());
        Files.writeString(Path.of("12_weakened_modularity_smartgrid"), report.toString());
    }

    /*
        @Test
        public void unused_abstractionReport() throws IOException {
            Report report =
                    new SpeculativeGenerality()
                            .analyze(
                                    project.getLanguage(),
                                    project.getSimulatorModel(),
                                    new SpeculativeGenerality().getSettings());
            Files.writeString(Path.of("unused_Abstraction"), report.toString());
        }
    */

    @Test
    void brokenModularity() {
        Report report =
                new BrokenModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        int a = 3;
    }

    @Test
    void deficient_encapsulation() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        Files.writeString(Path.of("deficient_encapsulation.md"), report.getDescription());
        int a = 3;
    }

    @Test
    void deficient_encapsulation_repair() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        int a = 3;
    }

    @Test
    void degraded_modularity() throws IOException {
        Report report =
                new DegradedModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DegradedModularity().getSettings());
        Files.writeString(Path.of("degraded_modularity.md"), report.getDescription());
        int a = 3;
    }

    /*
    @Test
    void evaluateScenario() {
            String badSmell = "deficient_encapsulation";
            String scenarioNumber = "01";
            boolean isBefore = false;
            String path = "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/smartGrid/"
                            + badSmell + "/" + "scenario_" + scenarioNumber;
            if (isBefore) {
                    path += "/before";
            } else {
                    path += "/after";
            }
            Result result = new Application().evaluate(CalculationMode.ONE_OFFSET, path);
            int a = 3;
            System.out.println("Complexity " + result.getComplexity().getValue());
            System.out.println("Cohesion " + result.getCohesion().getValue());
            System.out.println("Coupling " + result.getCoupling().getValue());
    }
    */
    // startTable("SmartGrid", "SmartGrid");
    @Test
    public void createTable() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(
                        "% "
                                + project.getSimulatorModel()
                                        .getComponents()
                                        .iterator()
                                        .next()
                                        .getTypes()
                                        .iterator()
                                        .next()
                                        .getPosition()
                                        .getFile()
                                        .getAbsolutePath())
                .append("\n");
        sb.append(toTableRow(broken_modularityReport_CSV())).append("\n");
        sb.append(toTableRow(deficient_encapsulationReport_CSV())).append("\n");
        sb.append(toTableRow(degraded_modularityReport_CSV())).append("\n");
        sb.append(toTableRow(duplicated_abstractionReport_CSV())).append("\n");
        sb.append(toTableRow(incomplete_abstractionReport_CSV())).append("\n");
        sb.append(toTableRow(missing_abstractionReport_CSV())).append("\n");
        sb.append(toTableRow(missing_modularityReport_CSV())).append("\n");
        sb.append(toTableRow(rebellious_modularityReport_CSV())).append("\n");
        sb.append(toTableRow(unexploited_HierarchyReport_CSV())).append("\n");
        sb.append(toTableRow(unused_abstractionReport_CSV())).append("\n");
        sb.append(toTableRow(weakened_modularityReport_CSV())).append("\n");
        sb.append(toTableRow(folded_hierarchyReport_CSV())).append("\n");
        System.out.println(sb.toString());
    }

    private String toTableRow(Report report) {
        return report.getText() + " & " + report.getNumberOfSmells() + " \\\\";
    }

    public Report broken_modularityReport_CSV() throws IOException {
        return new BrokenModularity()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new BrokenModularity().getSettings());
    }

    public Report deficient_encapsulationReport_CSV() throws IOException {
        return new DeficientEncapsulation()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new DeficientEncapsulation().getSettings());
    }

    public Report degraded_modularityReport_CSV() throws IOException {
        return new DegradedModularity()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new DegradedModularity().getSettings());
    }

    public Report duplicated_abstractionReport_CSV() throws IOException {
        return new DuplicateAbstraction()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new DuplicateAbstraction().getSettings());
    }

    public Report incomplete_abstractionReport_CSV() throws IOException {
        return new MissingHierarchy()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new MissingHierarchy().getSettings());
    }

    public Report missing_abstractionReport_CSV() throws IOException {
        return new MissingAbstraction()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new MissingAbstraction().getSettings());
    }

    public Report missing_modularityReport_CSV() throws IOException {
        return new MissingModularity()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new MissingModularity().getSettings());
    }

    public Report rebellious_modularityReport_CSV() throws IOException {
        return new LanguageBlob()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new LanguageBlob().getSettings());
    }

    public Report unexploited_HierarchyReport_CSV() throws IOException {
        return new UnexploitedHierarchy()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new UnexploitedHierarchy().getSettings());
    }

    public Report unused_abstractionReport_CSV() throws IOException {
        return new SpeculativeGenerality()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new SpeculativeGenerality().getSettings());
    }

    public Report weakened_modularityReport_CSV() throws IOException {
        return new DependencyCycle()
                .analyze(
                        project.getLanguage(),
                        project.getSimulatorModel(),
                        new DependencyCycle().getSettings());
    }

    public Report folded_hierarchyReport_CSV() throws IOException {
        var setting = new FoldedHierarchy().getSettings();
        setting.setValue("layer", "paradigm,domain,quality,analysis");
        return new FoldedHierarchy()
                .analyze(project.getLanguage(), project.getSimulatorModel(), setting);
    }
    // endtable
}

package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import abstraction.duplicated_abstraction.DuplicateAbstractionNeo4j;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mosim.refactorlizar.architecture.evaluation.Application;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.Result;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import modularity.broken_modularity.BrokenModularity;
import encapsulation.deficient_encapsulation.DeficientEncapsulation;
import modularity.degraded_modularity.DegradedModularity;
import hierarchy.folded_hierarchy.FoldedHierarchy;
import hierarchy.missing_hierarchy.MissingHierarchy;
import abstraction.missing_abstraction.MissingAbstraction;
import abstraction.unused_abstraction.UnusedAbstraction;
import modularity.missing_modularity.MissingModularity;
import modularity.rebellious_modularity.LanguageBlob;
import hierarchy.unexploited_Hierarchy.UnexploitedHierarchy;
import modularity.weakened_modularity.DependencyCycle;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;

public class SimulizarEval {

        private static Project project;
        private static final String analyzer = "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/simulizar/Palladio-Analyzer-SimuLizar";
        private static final String dsml =  "/Users/layornos/git/MartinWittinger-Masterarbeit-Daten-BadSmells-Analysis/simulizar/SimuLizar_SPRACHE";

        @BeforeAll
        public static void setUp() {
                List<String> simulatorPaths = new ArrayList<>();
                simulatorPaths.add(analyzer);
                List<String> languagePaths = new ArrayList<>();
                languagePaths.add(dsml);
                project = new ProjectParser().setLanguageKind(InputKind.ECLIPSE_PLUGIN)
                                .setSimulatorKind(InputKind.ECLIPSE_PLUGIN)
                                .addLanguagePaths(languagePaths).addSimulatorPaths(simulatorPaths)
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
                Report report = new DuplicateAbstractionNeo4j().analyze(project.getLanguage(),
                        project.getSimulatorModel(),
                        settings);
                Files.writeString(Path.of("01_duplicated_abstraction_simulizar"),
                        report.toString());
        }

        @Test
        public void missing_abstractionReport() throws IOException {
                Report report = new MissingAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(), 
                                new MissingAbstraction().getSettings());
                Files.writeString(Path.of("02_missing_abstraction_simulizar"), report.toString());
        }

        @Test
        public void unused_abstractionReport() throws IOException {
                Settings settings = new UnusedAbstraction().getSettings();
                settings.setValue("neo4j_uri", "bolt://localhost:7687");
                settings.setValue("neo4j_username", "neo4j");
                settings.setValue("neo4j_password", "test");
                Report report = new UnusedAbstraction().analyze(project.getLanguage(),
                        project.getSimulatorModel(),
                        settings);
                Files.writeString(Path.of("03_unused_abstraction_simulizar"), report.toString());
        }


        /*
         * ENCAPSULATION
         */
        @Test
        public void deficient_encapsulationReport() throws IOException {
                Report report = new DeficientEncapsulation().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new DeficientEncapsulation().getSettings());
                Files.writeString(Path.of("04_deficient_encapsulation_simulizar"), report.toString());
        }

        /*
         * HIERARCHY
         */
        @Test
        public void folded_hierarchyReport() throws IOException {
                var setting = new FoldedHierarchy().getSettings();
                setting.setValue("layer", "paradigm,domain,analysis,quality");
                Report report = new FoldedHierarchy().analyze(project.getLanguage(),
                        project.getSimulatorModel(), setting);
                Files.writeString(Path.of("05_folded_hierarchy_simulizar"), report.toString());
        }

        @Test
        public void missing_HierarchyReport() throws IOException {
                Report report = new MissingHierarchy().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new MissingHierarchy().getSettings());
                Files.writeString(Path.of("06_missing_hierarchy_simulizar"), report.toString());
        }

        @Test
        public void unexploited_HierarchyReport() throws IOException {
                Report report = new UnexploitedHierarchy().analyze(project.getLanguage(),
                        project.getSimulatorModel(),
                        new UnexploitedHierarchy().getSettings());
                Files.writeString(Path.of("07_unexploited_hierarchy_simulizar"), report.toString());
        }

        /*
         * MODULARITY
         */
        @Test
        public void broken_modularityReport() throws IOException {
                Report report = new BrokenModularity().analyze(project.getLanguage(),
                        project.getSimulatorModel(),
                        new BrokenModularity().getSettings());
                Files.writeString(Path.of("08_broken_modularity_simulizar"), report.toString());
        }

        @Test
        public void degraded_modularityReport() throws IOException {
                Report report = new DegradedModularity().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new DegradedModularity().getSettings());
                Files.writeString(Path.of("09_degraded_modularity_simulizar"), report.toString());
        }

        @Test
        public void missing_modularityReport() throws IOException {
                Report report = new MissingModularity().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new MissingModularity().getSettings());
                Files.writeString(Path.of("10_missing_modularity_simulizar"), report.toString());
        }

        @Test
        public void rebellious_modularityReport() throws IOException {
                Report report = new LanguageBlob().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new LanguageBlob().getSettings());
                Files.writeString(Path.of("11_rebellious_modularity_simulizar"), report.toString());
        }

        @Test
        public void weakened_modularityReport() throws IOException {
                Report report = new DependencyCycle().analyze(project.getLanguage(),
                        project.getSimulatorModel(), new DependencyCycle().getSettings());
                Files.writeString(Path.of("12_weakened_modularity_simulizar"), report.toString());
        }

        
        //startTable("Simulizar", "Simulizar");
        @Test
        public void createTable() throws IOException {
                StringBuilder sb = new StringBuilder();
                sb.append("% " + project.getSimulatorModel().getComponents().iterator().next().getTypes().iterator().next().getPosition().getFile().getAbsolutePath()).append("\n");
                sb.append(toTableRow(duplicated_abstractionReport_CSV())).append("\n");
                sb.append(toTableRow(missing_abstractionReport_CSV())).append("\n");
                sb.append(toTableRow(unused_abstractionReport_CSV())).append("\n");
                sb.append(toTableRow(deficient_encapsulationReport_CSV())).append("\n");
                sb.append(toTableRow(folded_hierarchyReport_CSV())).append("\n");
                sb.append(toTableRow(missing_hierarchyReport_CSV())).append("\n");
                sb.append(toTableRow(unexploited_HierarchyReport_CSV())).append("\n");
                sb.append(toTableRow(broken_modularityReport_CSV())).append("\n");
                sb.append(toTableRow(degraded_modularityReport_CSV())).append("\n");
                sb.append(toTableRow(missing_modularityReport_CSV())).append("\n");
                sb.append(toTableRow(rebellious_modularityReport_CSV())).append("\n");
                sb.append(toTableRow(weakened_modularityReport_CSV())).append("\n");
                System.out.println(sb.toString());
        }
        
        private String toTableRow(Report report) {
                return report.getText() + " & " + report.getNumberOfSmells() + " \\\\";
        }
        
        public Report broken_modularityReport_CSV() throws IOException {
               return new BrokenModularity().analyze(project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        }

        public Report deficient_encapsulationReport_CSV() throws IOException {
                return new DeficientEncapsulation().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new DeficientEncapsulation().getSettings());
        }

        public Report degraded_modularityReport_CSV() throws IOException {
                return new DegradedModularity().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new DegradedModularity().getSettings());
        }

        public Report duplicated_abstractionReport_CSV() throws IOException {
                return new DuplicateAbstractionNeo4j().analyze(project.getLanguage(),
                                project.getSimulatorModel(),
                                new DuplicateAbstractionNeo4j().getSettings());
        }

        @Test
        public Report missing_hierarchyReport_CSV() throws IOException {
                return new MissingHierarchy().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new MissingHierarchy().getSettings());
        }

        public Report missing_abstractionReport_CSV() throws IOException {
                return new MissingAbstraction().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new MissingAbstraction().getSettings());
        }

        public Report missing_modularityReport_CSV() throws IOException {
                return new MissingModularity().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new MissingModularity().getSettings());
        }

        public Report rebellious_modularityReport_CSV() throws IOException {
                return new LanguageBlob().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new LanguageBlob().getSettings());
        }

        public Report unexploited_HierarchyReport_CSV() throws IOException {
                return new UnexploitedHierarchy().analyze(project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnexploitedHierarchy().getSettings());
        }

        public Report unused_abstractionReport_CSV() throws IOException {
                return new UnusedAbstraction().analyze(project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnusedAbstraction().getSettings());
        }

        public Report weakened_modularityReport_CSV() throws IOException {
                return new DependencyCycle().analyze(project.getLanguage(),
                                project.getSimulatorModel(), new DependencyCycle().getSettings());
        }

        public Report folded_hierarchyReport_CSV() throws IOException {
                var setting = new FoldedHierarchy().getSettings();
                setting.setValue("layer", "domain,paradigm,analysis");
                return new FoldedHierarchy().analyze(project.getLanguage(),
                                project.getSimulatorModel(), setting);
        }
        // endtable

    void calculateMetric() {
            // Application application = new Application();
            // Result result = application.evaluate(CalculationMode.ONE_OFFSET,
            //                 "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/fixedBadSmells/deficient_encapsulation");
                            DecimalFormat df = new DecimalFormat("#.##");
            // System.out.println("Complexity: " + df.format(result.getComplexity().getValue()));
            // System.out.println("Cohesion: " + result.getCohesion().getValue());
            // System.out.println("Coupling: " + df.format(result.getCoupling().getValue()));
            // System.out.println("-----------------------------------------------------");
            // Complexity: 6035,21
            // Cohesion: -6.208100168616124E-6
            // Coupling: 1986,2
            Application application = new Application();
            Result result = application.evaluate(CalculationMode.ONE_OFFSET,
                             analyzer);
            System.out.println("Complexity: " + df.format(result.getComplexity().getValue()));
            System.out.println("Cohesion: " + result.getCohesion().getValue());
            System.out.println("Coupling: " + df.format(result.getCoupling().getValue()));
        }
}

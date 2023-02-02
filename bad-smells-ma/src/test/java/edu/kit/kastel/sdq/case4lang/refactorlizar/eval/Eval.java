package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import abstraction.duplicated_abstraction.DuplicateAbstraction;
import abstraction.missing_abstraction.MissingAbstraction;
import abstraction.unused_abstraction.SpeculativeGenerality;
import com.google.common.flogger.FluentLogger;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.InputKind;
import edu.kit.kastel.sdq.case4lang.refactorlizar.core.ProjectParser;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import encapsulation.deficient_encapsulation.DeficientEncapsulation;
import hierarchy.folded_hierarchy.FoldedHierarchy;
import hierarchy.missing_hierarchy.MissingHierarchy;
import hierarchy.unexploited_Hierarchy.UnexploitedHierarchy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import modularity.broken_modularity.BrokenModularity;
import modularity.degraded_modularity.DegradedModularity;
import modularity.missing_modularity.MissingModularity;
import modularity.rebellious_modularity.LanguageBlob;
import modularity.weakened_modularity.DependencyCycle;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Eval {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private Project project;
    private String scenarioNumber = "05";
    private String projectName = "./camunda";

    @BeforeEach
    public void setUp() {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(
                "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/mKAMP-eval-scenarios/scenario"
                        + scenarioNumber
                        + "/before");
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(
                "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/mKAMP-eval-scenarios/language_modularized");
        project =
                new ProjectParser()
                        .setLanguageKind(InputKind.FEATURE_FILE)
                        .setSimulatorKind(InputKind.FEATURE_FILE)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .parse();
        Path.of(projectName, scenarioNumber).toFile().mkdirs();
        System.out.println(
                "Project: "
                        + project.getSimulatorModel().getComponents().stream()
                                .flatMap(v -> v.getTypes().stream())
                                .count());
    }

    public static void main(String[] args) throws Exception {
        // new Eval().camunda();
        // new Eval().kamp();
        new Eval()
                .eval(
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/Palladio-Analyzer-SimuLizar",
                        "simuLizarTestResults",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/SimuLizar_SPRACHE",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/historicalScenarios",
                        200);
        new Eval()
                .eval(
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/mCamundaCleaned",
                        "camundaTestResults",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/sprache",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/historicalScenarios",
                        200);
        // copyRepoWithChangedFilesForGivenCommit("C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni
        // zeugs/Masterarbeit/Projekte/mSimuLizar",
        //                 Path.of("simuLizarTestResults"),
        // "ccd3d28f84c83ae110cad4b9c0c6720d9cc92d98");
        new Eval()
                .eval(
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/KAMP4APS_fürHistorySuche",
                        "kampTestResults",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/KAMP_Sprache",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/historicalScenarios",
                        200);
        new Eval()
                .eval(
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/smartGrid/Smart-Grid-ICT-Resilience-Framework_ANALYSE",
                        "Smart-Grid-ICT-Test",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/smartGrid/Smart-Grid-ICT-Resilience-Framework_SPRACHE",
                        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/smartGrid/historicalScenarios",
                        300);
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

    private void kamp() throws IOException {
        for (int i = 1; i <= 10; i++) {
            scenarioNumber = "0" + String.valueOf(i);
            if (i >= 10) {
                scenarioNumber = String.valueOf(i);
            }
            List<String> simulatorPaths = new ArrayList<>();
            String e =
                    "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/mKAMP-eval-scenarios/scenario"
                            + scenarioNumber
                            + "/before";
            if (!new File(e).exists()) {
                continue;
            }
            simulatorPaths.add(e);
            List<String> languagePaths = new ArrayList<>();
            languagePaths.add(
                    "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/KAMP/mKAMP-eval-scenarios/language_modularized");
            project =
                    new ProjectParser()
                            .setLanguageKind(InputKind.FEATURE_FILE)
                            .setSimulatorKind(InputKind.FEATURE_FILE)
                            .addLanguagePaths(languagePaths)
                            .addSimulatorPaths(simulatorPaths)
                            .parse();
            Path.of(projectName, scenarioNumber).toFile().mkdirs();
            System.out.println(
                    "Project: "
                            + project.getSimulatorModel().getComponents().stream()
                                    .flatMap(v -> v.getTypes().stream())
                                    .count());
            broken_modularityReport();
            deficient_encapsulationReport();
            degraded_modularityReport();
            duplicated_abstractionReport();
            incomplete_abstractionReport();
            missing_abstractionReport();
            missing_modularityReport();
            rebellious_modularityReport();
            unexploited_HierarchyReport();
            unused_abstractionReport();
            weakened_modularityReport();
        }
    }

    private void camunda() throws IOException {
        Map<Integer, List<Report>> result = new HashMap<>();
        for (int i = 1; i <= 30; i++) {
            scenarioNumber = "0" + String.valueOf(i);
            if (i >= 10) {
                scenarioNumber = String.valueOf(i);
            }
            List<String> simulatorPaths = new ArrayList<>();
            String e =
                    "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/scenarios/scenario"
                            + scenarioNumber
                            + "/langApply";
            if (!new File(e).exists()) {
                continue;
            }
            simulatorPaths.add(e);
            List<String> languagePaths = new ArrayList<>();
            languagePaths.add(
                    "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/mCamundaSprache");
            project =
                    new ProjectParser()
                            .setLanguageKind(InputKind.FEATURE_FILE)
                            .setSimulatorKind(InputKind.FEATURE_FILE)
                            .addLanguagePaths(languagePaths)
                            .addSimulatorPaths(simulatorPaths)
                            .parse();
            Path.of(projectName, scenarioNumber).toFile().mkdirs();
            System.out.println(
                    "Project: "
                            + project.getSimulatorModel().getComponents().stream()
                                    .flatMap(v -> v.getTypes().stream())
                                    .count());
            broken_modularityReport();
            deficient_encapsulationReport();
            degraded_modularityReport();
            duplicated_abstractionReport();
            incomplete_abstractionReport();
            missing_abstractionReport();
            missing_modularityReport();
            rebellious_modularityReport();
            unexploited_HierarchyReport();
            unused_abstractionReport();
            weakened_modularityReport();
            List<Report> reports = new ArrayList<>();
            reports.add(broken_modularityReport_CSV());
            reports.add(deficient_encapsulationReport_CSV());
            reports.add(degraded_modularityReport_CSV());
            reports.add(duplicated_abstractionReport_CSV());
            reports.add(incomplete_abstractionReport_CSV());
            reports.add(missing_abstractionReport_CSV());
            reports.add(missing_modularityReport_CSV());
            reports.add(rebellious_modularityReport_CSV());
            reports.add(unexploited_HierarchyReport_CSV());
            reports.add(unused_abstractionReport_CSV());
            reports.add(weakened_modularityReport_CSV());
            reports.add(folded_hierarchyReport_CSV());
            result.put(i, reports);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Scenario;");
        sb.append(
                        result.entrySet().iterator().next().getValue().stream()
                                .map(v -> v.getTitle())
                                .collect(Collectors.joining(";")))
                .append("\n");
        List<Entry<Integer, List<Report>>> list = new ArrayList<>(result.entrySet());
        list.sort((o1, o2) -> Integer.compare(o1.getKey(), o2.getKey()));
        for (Map.Entry<Integer, List<Report>> entry : list) {
            sb.append(entry.getKey()).append(";");
            sb.append(
                            entry.getValue().stream()
                                    .map(v -> String.valueOf(v.getNumberOfSmells()))
                                    .collect(Collectors.joining(";")))
                    .append("\n");
        }
        Files.writeString(Path.of("result.csv"), sb);
    }

    @Test
    public void broken_modularityReport() throws IOException {
        Report report =
                new BrokenModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new BrokenModularity().getSettings());
        Path path = Path.of(projectName, scenarioNumber, "broken_modularity");
        Files.writeString(path, report.toString(), StandardOpenOption.CREATE);
    }

    @Test
    public void deficient_encapsulationReport() throws IOException {
        Report report =
                new DeficientEncapsulation()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DeficientEncapsulation().getSettings());
        Path path = Path.of(projectName, scenarioNumber, "deficient_encapsulation");
        Files.writeString(path, report.toString());
    }

    @Test
    public void degraded_modularityReport() throws IOException {
        Report report =
                new DegradedModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DegradedModularity().getSettings());
        Path path = Path.of(projectName, scenarioNumber, "degraded_modularity.md");
        Files.writeString(path, report.toString());
    }

    @Test
    public void duplicated_abstractionReport() throws IOException {
        Report report =
                new DuplicateAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DuplicateAbstraction().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "duplicated_abstraction"), report.toString());
    }

    @Test
    public void incomplete_abstractionReport() throws IOException {
        Report report =
                new MissingHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingHierarchy().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "incomplete_abstraction"), report.toString());
    }

    @Test
    public void missing_abstractionReport() throws IOException {
        Report report =
                new MissingAbstraction()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingAbstraction().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "missing_abstraction"), report.toString());
    }

    @Test
    public void missing_modularityReport() throws IOException {
        Report report =
                new MissingModularity()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new MissingModularity().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "missing_modularity"), report.toString());
    }

    @Test
    public void rebellious_modularityReport() throws IOException {
        Report report =
                new LanguageBlob()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new LanguageBlob().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "rebellious_modularity"), report.toString());
    }

    @Test
    public void unexploited_HierarchyReport() throws IOException {
        Report report =
                new UnexploitedHierarchy()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new UnexploitedHierarchy().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "unexploited_Hierarchy"), report.toString());
    }

    @Test
    public void unused_abstractionReport() throws IOException {
        Report report =
                new SpeculativeGenerality()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new SpeculativeGenerality().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "unused_Abstraction"), report.toString());
    }

    @Test
    public void weakened_modularityReport() throws IOException {
        Report report =
                new DependencyCycle()
                        .analyze(
                                project.getLanguage(),
                                project.getSimulatorModel(),
                                new DependencyCycle().getSettings());
        Files.writeString(
                Path.of(projectName, scenarioNumber, "weakened_modularity"), report.toString());
    }

    public void eval(String repoPath, String targetPath, String languagePath, String resultPath)
            throws IOException, InterruptedException {
        eval(repoPath, targetPath, languagePath, resultPath, Integer.MAX_VALUE);
    }

    public void eval(
            String repoPath, String targetPath, String languagePath, String resultPath, int limit)
            throws IOException, InterruptedException {
        var it = walkCommits(repoPath).iterator();
        Path resultsFolder = Path.of(resultPath);
        ExecutorService executor = Executors.newWorkStealingPool(20);
        int i = 0;
        AtomicInteger counter = new AtomicInteger(0);
        while (it.hasNext()) {
            if (i > limit) {
                break;
            }
            RevCommit commit = it.next();
            if (commit.getParentCount() == 0) {
                continue;
            }
            logger.atWarning().log("Analyzing commit %s number %s", commit.getName(), i);
            i++;
            executor.execute(
                    () -> {
                        try {
                            if (counter.get() > 10) {
                                executor.shutdownNow();
                                return;
                            }
                            String commitId = commit.getId().name();
                            Path changedRepoPath =
                                    copyRepoWithChangedFilesForGivenCommit(
                                            repoPath, Path.of(targetPath), commitId);
                            if (changedRepoPath == null) {
                                return;
                            }
                            project =
                                    buildProject(
                                            languagePath,
                                            changedRepoPath.toString(),
                                            InputKind.FEATURE_FILE);
                            List<Report> reports = new ArrayList<>();
                            // reports.add(deficient_encapsulationReport_CSV());
                            // reports.add(degraded_modularityReport_CSV());
                            // reports.add(duplicated_abstractionReport_CSV());
                            // reports.add(incomplete_abstractionReport_CSV());
                            // reports.add(missing_abstractionReport_CSV());
                            // reports.add(rebellious_modularityReport_CSV());
                            // reports.add(unused_abstractionReport_CSV());
                            // reports.add(weakened_modularityReport_CSV());
                            reports.add(folded_hierarchyReport_CSV());
                            Files.walk(changedRepoPath)
                                    .filter(v -> v.getFileName().toString().endsWith("git"))
                                    .forEach(
                                            v -> {
                                                try {
                                                    System.err.println("Deleting " + v);
                                                    FileUtils.deleteDirectory(v.toFile());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                            for (Report report : reports) {
                                if (report.getNumberOfSmells() > 0) {
                                    Path path =
                                            Path.of(
                                                    resultsFolder.toString(),
                                                    report.getTitle(),
                                                    changedRepoPath.getFileName().toString(),
                                                    "before");

                                    FileUtils.copyDirectory(
                                            changedRepoPath.toFile(), path.toFile());
                                    Path pathAfter =
                                            Path.of(
                                                    resultsFolder.toString(),
                                                    report.getTitle(),
                                                    changedRepoPath.getFileName().toString(),
                                                    "after");

                                    FileUtils.copyDirectory(
                                            changedRepoPath.toFile(), pathAfter.toFile());
                                }
                            }

                            FileUtils.deleteDirectory(changedRepoPath.toFile());
                            counter.incrementAndGet();
                            System.out.println(counter.get() + " commits analyzed");
                        } catch (Exception e) {
                            logger.atWarning().log(
                                    "Error while analyzing commit %s", commit.getName());
                            e.printStackTrace();
                        }
                    });
        }
        System.out.println("Waiting for all threads to finish");
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.DAYS);
    }

    private Project buildProject(String languagePath, String simulatorPath, InputKind inputKind) {
        List<String> simulatorPaths = new ArrayList<>();
        simulatorPaths.add(simulatorPath);
        List<String> languagePaths = new ArrayList<>();
        languagePaths.add(languagePath);
        project =
                new ProjectParser()
                        .setLanguageKind(inputKind)
                        .setSimulatorKind(inputKind)
                        .addLanguagePaths(languagePaths)
                        .addSimulatorPaths(simulatorPaths)
                        .parse();
        return project;
    }

    public static Iterable<RevCommit> walkCommits(String repoPath) {
        try {
            Git git = Git.open(new File(repoPath));
            return git.log().call();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param repoPath the path to the repository.
     * @param resultDir the directory where the results will be stored.
     * @param commitHash the commit hash of the commit to analyze.
     * @return the path to the directory where the results were stored. The directory will be
     *     created if it does not exist. Null if no java files are changed.
     */
    public static Path copyRepoWithChangedFilesForGivenCommit(
            String repoPath, Path resultDir, String commitHash) {
        try {
            List<DiffEntry> entries = getDiff(repoPath, commitHash);
            Set<String> set = getChangedFileNames(entries);
            if (set.isEmpty()) {
                return null;
            }
            Path targetPath = Path.of(resultDir.toString(), commitHash);
            System.out.println(targetPath);
            copyToTargetPath(repoPath, targetPath);
            removeUnchangedJavaFiles(set, targetPath);
            Files.walk(targetPath)
                    .filter(v -> !Files.isDirectory(v))
                    .filter(v -> !v.toString().endsWith(".java"))
                    .filter(v -> !v.toString().contains("META"))
                    .forEach(
                            t -> {
                                try {
                                    Files.delete(t);
                                } catch (IOException e) {
                                }
                            });
            Files.walk(targetPath)
                    .sorted(Comparator.reverseOrder())
                    .filter(v -> Files.isDirectory(v))
                    .filter(
                            v -> {
                                try {
                                    return FileUtils.isEmptyDirectory(v.toFile());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            })
                    .forEach(
                            v -> {
                                try {
                                    Files.delete(v);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
            ;
            return targetPath;
        } catch (Exception e) {
            logger.atSevere().log(
                    "Error while copying repo with changed files for commit: " + commitHash);
        }
        return null;
    }

    private static void removeUnchangedJavaFiles(Set<String> set, Path targetPath)
            throws IOException {
        Files.walk(targetPath)
                .filter(v -> v.toString().endsWith(".java"))
                .filter(v -> !set.contains(v.getFileName().toString()))
                .forEach(
                        v -> {
                            try {
                                Files.delete(v);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
    }

    private static void copyToTargetPath(String repoPath, Path targetPath) throws IOException {
        FileUtils.copyDirectory(Path.of(repoPath).toFile(), targetPath.toFile());
    }

    private static List<DiffEntry> getDiff(String repoPath, String commitHash)
            throws IOException, MissingObjectException, IncorrectObjectTypeException {
        Git git = Git.open(Path.of(repoPath).toFile());
        RevWalk rw = new RevWalk(git.getRepository());
        RevCommit commit = rw.parseCommit(ObjectId.fromString(commitHash));
        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        ObjectId oldTree = commit.getTree();
        oldTreeIter.reset(reader, oldTree);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        ObjectId newTree =
                commit.getParentCount() > 0
                        ? rw.parseCommit(commit.getParent(0)).getTree().getId()
                        : Constants.EMPTY_TREE_ID;
        newTreeIter.reset(reader, newTree);
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(git.getRepository());
        List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
        rw.close();
        diffFormatter.close();
        return entries;
    }

    private static Set<String> getChangedFileNames(List<DiffEntry> entries) {
        return entries.stream()
                .filter(v -> !v.getChangeType().equals(ChangeType.DELETE))
                .map(v -> v.getNewPath())
                .map(v -> v.substring(v.lastIndexOf("/") + 1))
                .collect(Collectors.toSet());
    }
}

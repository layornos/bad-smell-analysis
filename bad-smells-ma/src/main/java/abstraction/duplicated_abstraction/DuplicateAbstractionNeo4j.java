package abstraction.duplicated_abstraction;

import static org.neo4j.driver.Values.parameters;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class DuplicateAbstractionNeo4j extends AbstractAnalyzer {
    private Set<CtType<?>> languageNodes;
    private Set<CtType<?>> analyzerNodes;
    private int threshold;
    private String neo4j_uri;
    private String neo4j_username;
    private String neo4j_password;

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {

        fillSettings(settings);
        Driver driver =
                GraphDatabase.driver(neo4j_uri, AuthTokens.basic(neo4j_username, neo4j_password));
        init_database(language, simulatorModel, driver);
        var analyzerClassNodes = getAllAnalyzerClassNames(driver);
        var subgraphs = getAllSubgraphs(driver, analyzerClassNodes);
        driver.close();

        var pairs_of_similar_dependencies = getDuplicatedDependencyStructure(subgraphs);
        pairs_of_similar_dependencies = remove_duplicates(pairs_of_similar_dependencies);

        return createReportFromAnalysisTypes(pairs_of_similar_dependencies);
    }

    private Set<PairOfAnalysisClasses> remove_duplicates(
            Set<PairOfAnalysisClasses> pairs_of_similar_dependencies) {
        Set<PairOfAnalysisClasses> result = new HashSet<>();
        for (PairOfAnalysisClasses pair : pairs_of_similar_dependencies) {
            if (result.isEmpty()) result.add(pair);
            else {
                boolean is_duplicate = false;
                for (PairOfAnalysisClasses pair1 : result) {
                    if (pair1.equals(pair)) is_duplicate = true;
                }
                if (!is_duplicate) result.add(pair);
            }
        }
        return result;
    }

    /*
     * @ param supgraphs
     * The subgraphs where the key is the analysis class
     * and the value is the set of all language classes it depends on
     */
    private Set<PairOfAnalysisClasses> getDuplicatedDependencyStructure(
            Map<String, Set<String>> subgraphs) {
        // var pairsOfSimilarDependencies = new HashMap<String, String>();
        final Set<PairOfAnalysisClasses> p = new HashSet<>();
        subgraphs.forEach(
                (k1, v1) -> {
                    subgraphs.forEach(
                            (k, v) -> {
                                int count = 0;
                                List<String> common_dependencies = null;
                                if (!k1.equals(k)) {
                                    count = (int) v.stream().filter(v1::contains).count();
                                    common_dependencies =
                                            v.stream()
                                                    .filter(v1::contains)
                                                    .collect(Collectors.toList());
                                }

                                if (count > threshold) {
                                    p.add(new PairOfAnalysisClasses(k, k1, common_dependencies));
                                }
                            });
                });
        return p;
    }

    private static class PairOfAnalysisClasses {
        public String first;
        public String second;
        public List<String> common_dependencies;

        public PairOfAnalysisClasses(
                String first, String second, List<String> common_dependencies) {
            this.first = first;
            this.second = second;
            this.common_dependencies = common_dependencies;
        }

        @Override
        public int hashCode() {
            return first.hashCode() + 31 * second.hashCode() + 31 * common_dependencies.size();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PairOfAnalysisClasses)) {
                return false;
            }
            PairOfAnalysisClasses other = (PairOfAnalysisClasses) obj;
            boolean dependencies_are_equal = false;
            if (this.common_dependencies.size() == other.common_dependencies.size()) {
                dependencies_are_equal = true;
                for (int i = 0; i < this.common_dependencies.size(); i++) {
                    if (!this.common_dependencies
                            .get(i)
                            .contains(other.common_dependencies.get(i))) {
                        dependencies_are_equal = false;
                        break;
                    }
                }
            }
            return other.first.equals(second)
                    && other.second.equals(first)
                    && dependencies_are_equal;
        }
    }

    private Report createReportFromAnalysisTypes(
            Set<PairOfAnalysisClasses> pairs_of_similar_dependencies) {
        String title = "Duplicate Abstraction";
        var description = createReportHeader(pairs_of_similar_dependencies.size());
        pairs_of_similar_dependencies.forEach(
                pair -> {
                    description.append(
                            String.format(
                                    "The analysis types %s and %s depend on the following language types:\n",
                                    pair.first, pair.second));
                    pair.common_dependencies.forEach(
                            languageType -> {
                                description.append(String.format("%s\n", languageType));
                            });
                    description.append("\n\n");
                });
        return new Report(
                title,
                description.toString(),
                !pairs_of_similar_dependencies.isEmpty(),
                pairs_of_similar_dependencies.size());
    }

    private StringBuilder createReportHeader(int size) {
        StringBuilder description = new StringBuilder();
        description.append(String.format("%s duplicated dependency structures found +\n\n", size));
        description.append(
                String.format(
                        "For the thershold of %s the following structures were found:\n\n",
                        threshold));
        return description;
    }

    private void fillSettings(Settings settings) {
        if (settings.getSetting("threshold").isPresent())
            this.threshold = Integer.parseInt(settings.getSetting("threshold").get().getValue());
        if (settings.getSetting("neo4j_uri").isPresent())
            this.neo4j_uri = settings.getSetting("neo4j_uri").get().getValue();
        if (settings.getSetting("neo4j_username").isPresent())
            this.neo4j_username = settings.getSetting("neo4j_username").get().getValue();
        if (settings.getSetting("neo4j_password").isPresent())
            this.neo4j_password = settings.getSetting("neo4j_password").get().getValue();
    }

    private Map<String, Set<String>> getAllSubgraphs(
            Driver driver, Set<String> analyzerClassNodes) {
        Map<String, Set<String>> graphs = new HashMap<>();
        analyzerClassNodes.stream()
                .forEach(
                        node -> {
                            graphs.put(node, getSubgraph(driver, node));
                        });
        graphs.values().removeIf(Set::isEmpty);
        return graphs;
    }

    private void clean_database(Session session) {
        session.writeTransaction(
                tx -> {
                    tx.run("MATCH (n)\nDETACH DELETE n");
                    System.out.println("cleaned database");
                    return Void.TYPE;
                });
    }

    private void create_analyzer_nodes(Session session, CtType<?> analyzerNode) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "CREATE (a:AnalyzerClass {name:$fqn})",
                            parameters("fqn", analyzerNode.getQualifiedName()));
                    return "added analyzer nodes";
                });
    }

    private void create_language_nodes(Session session, CtTypeReference<?> v) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "MERGE (a:LanguageClass {name: $fqn})",
                            parameters("fqn", v.getQualifiedName()));
                    return Void.TYPE;
                });
    }

    private void create_language_nodes_and_relations_to_analyzer_nodes(
            Session session, CtType<?> analyzerNode) {
        analyzerNode.getReferencedTypes().stream()
                .filter(
                        v -> {
                            var type_declaration = v.getTypeDeclaration();
                            if (languageNodes.contains(type_declaration)) return true;
                            return false;
                        })
                .forEach(
                        v -> {
                            create_language_nodes(session, v);
                            create_language_to_analyzer_class_relations(session, analyzerNode, v);
                        });
    }

    private void create_language_to_analyzer_class_relations(
            Session session, CtType<?> type, CtTypeReference<?> v) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "MATCH (a:AnalyzerClass), (b:LanguageClass) WHERE a.name =$analyzerClassName AND b.name = $languageClassName CREATE (a)-[r:uses]->(b)",
                            parameters(
                                    "analyzerClassName",
                                    type.getQualifiedName(),
                                    "languageClassName",
                                    v.getQualifiedName()));
                    return "add relation";
                });
    }

    private Set<String> getSubgraph(Driver driver, String analysisClassName) {
        try (Session session = driver.session()) {
            // get analysisclass and all connected languages
            // MATCH (a:AnalyzerClass {name: $name })-[r]-(b) RETURN type(r), a, b
            return session.readTransaction(
                    tx -> {
                        var connectedLanguageNodes = new HashSet<String>();
                        Result result =
                                tx.run(
                                        "MATCH (:AnalyzerClass {name: $name })-[]-(b) RETURN b.name",
                                        parameters("name", analysisClassName));
                        while (result.hasNext()) {
                            connectedLanguageNodes.add(result.next().get(0).asString());
                        }
                        // connectedLanguageNodes.stream().forEach(node ->
                        // System.out.println(node));
                        return connectedLanguageNodes;
                    });
        }
    }

    private Set<String> getAllAnalyzerClassNames(Driver driver) {
        try (Session session = driver.session()) {
            return session.readTransaction(
                    tx -> {
                        var analyzerClassNodes = new HashSet<String>();
                        Result result = tx.run("MATCH (a:AnalyzerClass) RETURN a.name");
                        while (result.hasNext()) {
                            analyzerClassNodes.add(result.next().get(0).asString());
                        }
                        return analyzerClassNodes;
                    });
        }
    }

    private void init_database(
            ModularLanguage language, SimulatorModel simulatorModel, Driver driver) {
        try (Session session = driver.session()) {
            clean_database(session);
            init_language_and_analysis(language, simulatorModel);

            for (CtType<?> analyzerNode : analyzerNodes) {
                create_analyzer_nodes(session, analyzerNode);
                create_language_nodes_and_relations_to_analyzer_nodes(session, analyzerNode);
            }
        }
    }

    private void init_language_and_analysis(
            ModularLanguage language, SimulatorModel simulatorModel) {
        this.languageNodes =
                language.getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());
        this.analyzerNodes =
                simulatorModel.getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());
    }

    @Override
    public Settings getSettings() {
        return new Settings.SettingsBuilder()
                .addSetting(
                        "threshold",
                        true,
                        "Sets threshold for number of classes that have the same dependency structure")
                .addSetting("neo4j_uri", true, "Uri to the running Neo4J Database")
                .addSetting("neo4j_username", true, "Login username for the Neo4J Instance")
                .addSetting("neo4j_password", true, "Login password for the Neo4J Instance")
                .build();
    }
}

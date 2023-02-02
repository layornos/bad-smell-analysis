package abstraction.duplicated_abstraction;

import static org.neo4j.driver.Values.parameters;

import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import java.util.HashMap;
import java.util.HashSet;
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

public class GraphIsomorphism {

    private Set<CtType<?>> languageNodes;
    private Set<CtType<?>> analyzerNodes;
    private Map<Long, Map<String, String>> isomorphisms;

    public GraphIsomorphism() {
        isomorphisms = new HashMap<>();
    }

    public void start(Project project, int threshold) {
        Driver driver =
                GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "test"));
        init_database(project, driver);
        var analyzerClassNodes = getAllAnalyzerClassNames(driver);
        var subgraphs = getAllSubgraphs(driver, analyzerClassNodes);

        // get one analyzer class and all language classes related
        // add one tmp analyzer class node and all language class node to the database
        // subgraph isomorphism analysis tmp in rest?
        // var subgraph = getSubgraph(driver,
        // "smartgrid.attackersimulation.strategies.AttackStrategies");
        var pairs_of_similar_dependencies = new HashMap<String, String>();
        subgraphs.forEach(
                (k1, v1) -> {
                    subgraphs.forEach(
                            (k, v) -> {
                                int count = 0;
                                if (!k1.equals(k)) {
                                    count = (int) v.stream().filter(v1::contains).count();
                                }

                                if (count > threshold) {
                                    pairs_of_similar_dependencies.put(k, k1);
                                }
                            });
                });
        pairs_of_similar_dependencies.forEach(
                (k, v) -> {
                    System.out.println("Similar Graphs: " + k + " and " + v);
                });
        driver.close();
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

    private void create_analyzer_nodes(Session session, CtType<?> type) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "CREATE (a:AnalyzerClass {name:$fqn})",
                            parameters("fqn", type.getQualifiedName()));
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
            Session session, CtType<?> type) {
        type.getReferencedTypes().stream()
                .filter(v -> languageNodes.contains(v.getTypeDeclaration()))
                .forEach(
                        v -> {
                            create_language_nodes(session, v);
                            create_language_to_analyzer_class_relations(session, type, v);
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

    private void init_database(Project project, Driver driver) {
        try (Session session = driver.session()) {
            clean_database(session);
            init_language_and_analysis(project);

            for (CtType<?> type : analyzerNodes) {
                create_analyzer_nodes(session, type);
                create_language_nodes_and_relations_to_analyzer_nodes(session, type);
            }
        }
    }

    private void init_language_and_analysis(Project project) {
        this.languageNodes =
                project.getLanguage().getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());
        this.analyzerNodes =
                project.getSimulatorModel().getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());
    }

    private void graph_isomorphism_analysis(Driver driver, Map<String, Set<String>> subgraphs) {
        try (Session session = driver.session()) {
            subgraphs.forEach(
                    (k, v) -> {
                        var languageClasses =
                                session.writeTransaction(
                                        tx -> {
                                            tx.run(
                                                    "MATCH (s)-[*]->(n) WHERE s.name = $analyzerClassName "
                                                            + "remove s:AnalyzerClass set s:LabelName "
                                                            + "remove n:LanguageClass set n:LabelName",
                                                    parameters("analyzerClassName", k));
                                            return Void.TYPE;
                                        });
                    });
        }

        try (Session session = driver.session()) {
            subgraphs.forEach(
                    (k, v) -> {
                        session.writeTransaction(
                                tx -> {
                                    tx.run("CREATE (a:LabelName2 {name:$k})", parameters("k", k));
                                    return "added analyzer nodes subs";
                                });
                        v.stream()
                                .forEach(
                                        languageClassNode -> {
                                            session.writeTransaction(
                                                    tx -> {
                                                        tx.run(
                                                                "CREATE (a:LabelName2 {name:$v})",
                                                                parameters("v", v));
                                                        return "added language nodes subs";
                                                    });
                                        });
                        v.stream()
                                .forEach(
                                        languageClassNode -> {
                                            session.writeTransaction(
                                                    tx -> {
                                                        tx.run(
                                                                "MATCH (a:LabelName2), (b:LabelName2) WHERE a.name =$analyzerClassName AND b.name = $languageClassName MERGE (a)-[r:uses]->(b)",
                                                                parameters(
                                                                        "analyzerClassName",
                                                                        k,
                                                                        "languageClassName",
                                                                        v));
                                                        return "";
                                                    });
                                        });
                        session.readTransaction(
                                tx -> {
                                    var result =
                                            tx.run(
                                                    "CALL csb.subgraphIsomorphism($patternLabel, $targetLabel) "
                                                            + "YIELD subgraphIndex, patternNode, targetNode",
                                                    parameters(
                                                            "patternLabel",
                                                            "LabelName2",
                                                            "targetLabel",
                                                            "LabelName"));
                                    while (result.hasNext()) {
                                        var subgraphIndex =
                                                result.next().get("subgraphIndex").asLong();
                                        var patternNode =
                                                result.next()
                                                        .get("patternNode")
                                                        .asNode()
                                                        .get("name")
                                                        .asString();
                                        var targetNode =
                                                result.next()
                                                        .get("targetNode")
                                                        .asNode()
                                                        .get("name")
                                                        .asString();
                                        if (!isomorphisms.containsKey(subgraphIndex))
                                            isomorphisms.put(subgraphIndex, new HashMap<>());
                                        isomorphisms
                                                .get(subgraphIndex)
                                                .put(patternNode, targetNode);
                                    }
                                    return "";
                                });

                        session.writeTransaction(
                                tx -> {
                                    tx.run(
                                            "MATCH (s:LabelName2)-[*]->(n) WHERE s.name = \""
                                                    + k
                                                    + "\" detach delete s detach delete n");
                                    return "";
                                });
                        System.out.println(
                                "For "
                                        + k
                                        + " we found "
                                        + isomorphisms.keySet().size()
                                        + " isomorphisms.");
                    });
        }
    }
}

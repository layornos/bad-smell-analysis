package abstraction.unused_abstraction;

import static org.neo4j.driver.Values.parameters;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class UnusedAbstraction extends AbstractAnalyzer {

    // MATCH (l:LanguageClass)
    // WHERE NOT (l)-[:uses]-(:AnalyzerClass)
    // RETURN l
    private Set<CtType<?>> languageNodes;
    private Set<CtType<?>> analyzerNodes;
    private String neo4j_uri;
    private String neo4j_username;
    private String neo4j_password;

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getName() {
        return "null";
    }

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        fillSettings(settings);
        Driver driver =
                GraphDatabase.driver(neo4j_uri, AuthTokens.basic(neo4j_username, neo4j_password));
        init_database(language, simulatorModel, driver);

        var unused_language_types = extract_unused_language_types(driver);

        driver.close();
        return createReport(unused_language_types);
    }

    private Set<String> extract_unused_language_types(Driver driver) {
        try (Session session = driver.session()) {
            var unused_language_types = new HashSet<String>();
            return session.readTransaction(
                    tx -> {
                        var res =
                                tx.run(
                                        "MATCH (l:LanguageClass)"
                                                + "WHERE NOT (l)-[:uses]-(:AnalyzerClass)"
                                                + "RETURN l");
                        while (res.hasNext()) {
                            unused_language_types.add(
                                    res.next().get("l").asNode().get("name").asString());
                        }
                        return unused_language_types;
                    });
        }
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

    private void create_language_nodes(Session session, CtType<?> type) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "MERGE (a:LanguageClass {name: $fqn})",
                            parameters("fqn", type.getQualifiedName()));
                    return Void.TYPE;
                });
    }

    private void create_language_nodes_by_reference(Session session, CtTypeReference<?> type) {
        session.writeTransaction(
                tx -> {
                    tx.run(
                            "MERGE (a:LanguageClass {name: $fqn})",
                            parameters("fqn", type.getQualifiedName()));
                    return Void.TYPE;
                });
    }

    private void create_language_nodes_and_relations_to_analyzer_nodes(
            Session session, CtType<?> type) {
        type.getReferencedTypes().stream()
                .filter(v -> languageNodes.contains(v.getTypeDeclaration()))
                .forEach(
                        v -> {
                            create_language_nodes_by_reference(session, v);
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

    private void init_database(
            ModularLanguage language, SimulatorModel simulatorModel, Driver driver) {
        try (Session session = driver.session()) {
            clean_database(session);
            init_language_and_analysis(language, simulatorModel);

            analyzerNodes.forEach(analyzerNode -> create_analyzer_nodes(session, analyzerNode));
            languageNodes.stream()
                    .filter(this::is_regular_type)
                    .forEach(languageNode -> create_language_nodes(session, languageNode));
            analyzerNodes.forEach(
                    analyzerNode ->
                            create_language_nodes_and_relations_to_analyzer_nodes(
                                    session, analyzerNode));
        }
    }

    private boolean is_regular_type(CtType<?> type) {
        if (type.getQualifiedName().contains(".impl.") && type.getQualifiedName().endsWith("Impl"))
            return false;
        if (type.getQualifiedName().contains(".util.")
                && type.getQualifiedName().endsWith("Factory")) return false;
        if (type.getQualifiedName().contains(".util.")
                && type.getQualifiedName().endsWith("Switch")) return false;
        return true;
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

    private Report createReport(Set<String> unused_language_types) {
        String title = "Unused Abstraction";
        StringBuilder description = new StringBuilder();
        description.append(
                String.format("%s unused types found +\n\n", unused_language_types.size()));
        unused_language_types.forEach(
                type -> {
                    description.append(String.format("The language type %s is unused.\n", type));
                });
        return new Report(
                title,
                description.toString(),
                !unused_language_types.isEmpty(),
                unused_language_types.size());
    }

    private void fillSettings(Settings settings) {
        if (settings.getSetting("neo4j_uri").isPresent())
            this.neo4j_uri = settings.getSetting("neo4j_uri").get().getValue();
        if (settings.getSetting("neo4j_username").isPresent())
            this.neo4j_username = settings.getSetting("neo4j_username").get().getValue();
        if (settings.getSetting("neo4j_password").isPresent())
            this.neo4j_password = settings.getSetting("neo4j_password").get().getValue();
    }

    @Override
    public Settings getSettings() {
        return new Settings.SettingsBuilder()
                .addSetting("neo4j_uri", true, "Uri to the running Neo4J Database")
                .addSetting("neo4j_username", true, "Login username for the Neo4J Instance")
                .addSetting("neo4j_password", true, "Login password for the Neo4J Instance")
                .build();
    }
}

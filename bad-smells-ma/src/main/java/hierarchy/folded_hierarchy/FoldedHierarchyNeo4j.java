package hierarchy.folded_hierarchy;

import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Project;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;

public class FoldedHierarchyNeo4j {
    private Set<CtType<?>> languageNodes;
    private Set<CtType<?>> analyzerNodes;
    private Map<Long, Map<String,String>> isomorphisms;
    List<String> layers;

    public FoldedHierarchyNeo4j(){
        isomorphisms = new HashMap<>();
    }

    public void start(Project project) {
        layers =
                Arrays.asList("paradigm,domain,quality,analysis,experiment".split(","));
        Driver driver =
                GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "test"));
        init_database(project, driver);

        // NORMAL layer violation: MATCH (a:AnalyzerClass)-[]->(b:LanguageClass) where a.layer_number < b.layer_number return a,b
        // STRICT layer violation: MATCH (a:AnalyzerClass)-[]->(b:LanguageClass) where (a.layer_number - b.layer_number) > 0 return a,b

        try (Session session = driver.session()) {
            var nodesNormalLayerViolation = session.readTransaction(tx -> {
                var result = tx.run("MATCH (a:AnalyzerClass)-[]->(b:LanguageClass) where a.layer_number < b.layer_number return a.name,b.name");
                var sb = new StringBuilder("Normal Layer Violations:").append("\n");
                while(result.hasNext()){
                    var row = result.next();
                    sb.append(row.get("a.name").asString()).append(" to ").append(row.get("b.name")).append("\n");
                }
                return sb.toString();
            });

            var nodesStrictLayerViolation = session.readTransaction(tx -> {
                var result = tx.run("MATCH (a:AnalyzerClass)-[]->(b:LanguageClass) where (a.layer_number - b.layer_number) > 0 return a.name,b.name");
                var sb = new StringBuilder("Strict Layer Violations:").append("\n");
                while(result.hasNext()){
                    var row = result.next();
                    sb.append(row.get("a.name").asString()).append(" to ").append(row.get("b.name")).append("\n");
                }
                return sb.toString();
            });
            System.out.println(nodesNormalLayerViolation);
            System.out.println(nodesStrictLayerViolation);
        }

        driver.close();


    }

    private void init_database(Project project, Driver driver) {
        try (Session session = driver.session()) {
            clean_database(session);
            init_language_and_analysis(project);

            for (CtType<?> type : analyzerNodes) {
                create_analyzer_nodes(session, type);
                create_language_nodes_and_relations_to_analyzer_nodes(session,type);
            }

        }
    }

    private void clean_database(Session session) {
        session.writeTransaction(tx -> {
            tx.run("MATCH (n)\nDETACH DELETE n");
            System.out.println("cleaned database");
            return Void.TYPE;
        });
    }

    private void init_language_and_analysis(Project project) {
        this.languageNodes =
                project.getLanguage().getComponents().stream().flatMap(v -> v.getTypes().stream()).collect(Collectors.toSet());
        this.analyzerNodes =
                project.getSimulatorModel().getComponents().stream().flatMap(v -> v.getTypes().stream()).collect(Collectors.toSet());

    }

    private void create_analyzer_nodes(Session session, CtType<?> type) {
        String layer = getLayerOfType(type);
        session.writeTransaction(tx -> {
            tx.run("CREATE (a:AnalyzerClass {name:$fqn,layer:$layer,layer_number:$layer_number})",
                    parameters("fqn", type.getQualifiedName(), "layer", layer, "layer_number", getLayerNumber(layer)));
            return "added analyzer nodes";
        });
    }

    private void create_language_nodes(Session session, CtTypeReference<?> v) {
        String layer = getLayerOfType(v);
        session.writeTransaction(tx -> {
            tx.run("Merge (a:LanguageClass {name:$fqn,layer:$layer,layer_number:$layer_number})",
                    parameters("fqn", v.getQualifiedName(), "layer", layer, "layer_number", getLayerNumber(layer)));
            return Void.TYPE;
        });
    }

    private String getLayerOfType(CtTypeReference<?> type) {
        var layer = layers.stream().filter(l -> type.getQualifiedName().contains(l)).collect(Collectors.toList());
        if(layer.size() > 1) return "none";
        if(layer.isEmpty()) return "none";
        return layer.get(0);
    }
    private String getLayerOfType(CtType<?> type) {
        var layer = layers.stream().filter(l -> type.getQualifiedName().contains(l)).collect(Collectors.toList());
        if(layer.size() > 1) return "none";
        if(layer.isEmpty()) return "none";
        return layer.get(0);
    }

    private int getLayerNumber(String layer){
        for(int i = 0; i < layers.size(); i++){
            if(layers.get(i).equals(layer)) return i;
        }
        return -1;
    }


    private void create_language_nodes_and_relations_to_analyzer_nodes(Session session, CtType<?> type) {
        type.getReferencedTypes().stream()
                .filter(v -> languageNodes.contains(v.getTypeDeclaration())).forEach(v -> {
                    create_language_nodes(session, v);
                    create_language_to_analyzer_class_relations(session, type, v);
                });
    }

    private void create_language_to_analyzer_class_relations(Session session, CtType<?> type, CtTypeReference<?> v) {
        session.writeTransaction(tx -> {
            tx.run("MATCH (a:AnalyzerClass), (b:LanguageClass) WHERE a.name =$analyzerClassName AND b.name = $languageClassName CREATE (a)-[r:uses]->(b)", parameters("analyzerClassName", type.getQualifiedName(), "languageClassName", v.getQualifiedName()));
            return "add relation";
        });
    }

}

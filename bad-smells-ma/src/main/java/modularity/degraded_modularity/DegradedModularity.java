package modularity.degraded_modularity;

import com.google.common.graph.MutableNetwork;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.DependencyGraphSupplier;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.Edge;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.JavaUtils;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.graphs.TypeGraphs;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;

public class DegradedModularity extends AbstractAnalyzer {

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        MutableNetwork<CtType<?>, Edge<CtType<?>, CtTypeMember>> graph =
                DependencyGraphSupplier.getTypeGraph(language, simulatorModel);
        TypeGraphs.removeNonProjectNodes(language, simulatorModel, graph);
        TypeGraphs.removeEdgesWithoutLanguageTarget(language, graph);
        TypeGraphs.removeEdgesWithSimulatorAsTarget(graph, simulatorModel);
        removeNonScatter(graph, language, simulatorModel);
        return TypeLevelReportGeneration.generateReport(graph, simulatorModel, language);
    }

    private <T, R> boolean hasOnePredecessor(MutableNetwork<T, Edge<T, R>> graph, T type) {
        return graph.predecessors(type).size() < 2;
    }

    private <R> void removeNonScatter(
            MutableNetwork<CtType<?>, Edge<CtType<?>, R>> graph, ModularLanguage language, SimulatorModel simulator) {
        graph.nodes().stream()
                .filter(type -> JavaUtils.isLanguageType(language, type))
                .filter(
                        type ->
                                hasOnePredecessor(graph, type)
                                                        || isSameComponent(graph.predecessors(type),
                                                                        language)
                                        || isSameComponent(graph.predecessors(type),
                                                                        simulator))
                .collect(Collectors.toList())
                .forEach(graph::removeNode);
    }

    private <T> boolean isSameComponent(Set<T> types, ModularLanguage language) {
            return language.getComponents().stream().filter(
                            v -> types.stream().anyMatch(inner -> v.getTypes().contains(inner)))
                            .count() == 1;
    }
    
    private <T> boolean isSameComponent(Set<T> types, SimulatorModel simulatorModel) {
            return simulatorModel.getComponents().stream().filter(
                            v -> types.stream().anyMatch(inner -> v.getTypes().contains(inner)))
                            .count() == 1;
    }
}

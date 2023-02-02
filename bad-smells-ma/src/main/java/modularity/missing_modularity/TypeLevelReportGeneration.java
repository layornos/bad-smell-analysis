package modularity.missing_modularity;

import com.google.common.graph.MutableNetwork;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.Edge;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons_analyzer.JavaUtils;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;

public class TypeLevelReportGeneration {

    public static Report generateReport(
            MutableNetwork<CtType<?>, Edge<CtType<?>, CtTypeMember>> graph,
            SimulatorModel model,
            ModularLanguage language) {
        int count = graph.nodes().size();
        if (count == 0) {
            return new Report("Missing Modularity", "All types use layers", false);
        }
        StringBuilder description = new StringBuilder();
        description.append("There were " + count + " types not using layers found\n");
        graph.nodes().stream()
                .filter(type -> JavaUtils.isSimulatorType(model, type))
                .forEach(v -> description.append(v.getQualifiedName() + " uses no layer\n"));
        return new Report("Missing Modularity", description.toString(), true, count);
    }
}

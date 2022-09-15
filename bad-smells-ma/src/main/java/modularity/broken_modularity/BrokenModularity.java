package modularity.broken_modularity;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.List;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class BrokenModularity extends AbstractAnalyzer {

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        List<CtType<?>> types = getLanguageTypes(language);
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (CtType<?> ctType : types) {
            // Hol alle Typen des Simulators
            List<CtTypeReference<?>> col =
                    ctType.getReferencedTypes().stream()
                            .filter(
                                    v ->
                                            isSimulatorType(simulatorModel, v)
                                                    && isNotLanguageType(language, v))
                            .collect(Collectors.toList());
            // Entferne Referenzen
            col.remove(ctType.getReference());
            // FIXME: in methode auslagern
            for (CtTypeReference<?> dependency : col) {
                sb.append(ctType.getQualifiedName())
                        .append(" -> ")
                        .append(dependency.getQualifiedName())
                        .append("\n");
                counter++;
            }
        }
        System.out.println(sb.toString());
        return new Report("Broken Modularity", sb.toString(), counter > 0, counter);
    }

    private boolean isNotLanguageType(ModularLanguage language, CtTypeReference<?> v) {
        return language.getTypeWithQualifiedName(v.getQualifiedName()) == null;
    }

    private boolean isSimulatorType(SimulatorModel simulatorModel, CtTypeReference<?> v) {
        return simulatorModel.getTypeWithQualifiedName(v.getQualifiedName()) != null;
    }

    private List<CtType<?>> getLanguageTypes(ModularLanguage language) {
        return language.getComponents().stream()
                .flatMap(c -> c.getTypes().stream())
                .collect(Collectors.toList());
    }
}

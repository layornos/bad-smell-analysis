package abstraction.duplicated_abstraction;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Component;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

@Deprecated
public class DuplicateAbstraction extends AbstractAnalyzer {

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        Set<CtType<?>> types =
                simulatorModel.getComponents().stream()
                        .map(Component::getTypes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
        List<CtMethod<?>> clones = new ArrayList<>();
        for (CtType<?> origin : types) {
            for (CtType<?> target : types) {
                for (CtMethod<?> originalMethod : origin.getMethods()) {
                    if (target.getMethods().stream().anyMatch(v -> v.equals(originalMethod))
                            && isNotSameType(origin, target)
                            && isConcreteMethod(originalMethod)
                            && hasLanguageType(originalMethod, language)) {

                        clones.add(originalMethod);
                    }
                }
            }
        }
        String title = "Duplicate Abstraction";
        StringBuilder description = new StringBuilder();
        description.append(String.format("%s duplicated methods found +\n\n", clones.size()));
        for (CtMethod<?> ctMethod : clones) {
            description.append(
                    String.format(
                            "Method %s from type %s is duplicated%n",
                            ctMethod.getSignature(),
                            ctMethod.getDeclaringType().getQualifiedName()));
        }
        return new Report(title, description.toString(), !clones.isEmpty(), clones.size());
    }

    private boolean hasLanguageType(CtMethod<?> originalMethod, ModularLanguage language) {
        return originalMethod.getReferencedTypes().stream()
                .anyMatch(v -> language.getTypeWithQualifiedName(v.getQualifiedName()) != null);
    }

    private boolean isNotSameType(CtType<?> origin, CtType<?> target) {
        return !origin.equals(target);
    }

    private boolean isConcreteMethod(CtMethod<?> method) {
        return method.getBody() != null && !method.getBody().isImplicit();
    }
}

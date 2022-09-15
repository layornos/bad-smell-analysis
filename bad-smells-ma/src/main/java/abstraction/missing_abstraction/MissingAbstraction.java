package abstraction.missing_abstraction;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.Component;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;

public class MissingAbstraction extends AbstractAnalyzer {

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        List<CtMethod<?>> methods = new ArrayList<>();
        var types =
                simulatorModel.getComponents().stream()
                        .map(Component::getTypes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
        for (CtType<?> ctType : types) {
            for (CtMethod<?> method : ctType.getMethods()) {
                if (hasPrimitiveParameter(method)) {
                    methods.add(method);
                }
            }
        }
        String title = "Missing Abstraction";
        StringBuilder description = new StringBuilder();
        description.append(
                String.format("%s methods with primitive parameters found \n\n", methods.size()));
        for (CtMethod<?> ctMethod : methods) {
            description.append(
                    String.format(
                            "Method %s from type %s has primitive parameters%n",
                            ctMethod.getSignature(),
                            ctMethod.getDeclaringType().getQualifiedName()));
        }
        return new Report(title, description.toString(), !methods.isEmpty(), methods.size());
    }

    private boolean hasPrimitiveParameter(CtMethod<?> method) {
        return method.getParameters().stream().filter(this::isPrimitiveType).count() > 1;
    }

    private boolean isPrimitiveType(CtParameter<?> v) {
        return v.getType().equals(v.getFactory().Type().stringType()) || v.getType().isPrimitive();
    }
}

package abstraction.unused_abstraction;

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
import spoon.refactoring.MethodCallState;
import spoon.refactoring.MethodInvocationSearch;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.TypeFilter;

@Deprecated
public class SpeculativeGenerality extends AbstractAnalyzer {

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
        var analysisTypes =
                simulatorModel.getComponents().stream()
                        .map(Component::getTypes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

        List<CtElement> removable = new ArrayList<>();
        for (CtType<?> ctType : analysisTypes) {
            for (CtField<?> field : ctType.getFields()) {
                if (field.getSimpleName().equals("serialVersionUID")) {
                    continue;
                }
                if (field.isPrivate()) {
                    List<CtFieldAccess<?>> elements =
                            ctType.getElements(new FieldAccessFilter(field.getReference()));
                    if (elements.isEmpty()) {
                        removable.add(field);
                    }
                }
            }
            var search = new MethodInvocationSearch();
            ctType.accept(search);
            var refs = ctType.getElements(new TypeFilter<>(CtExecutableReference.class));
            for (CtMethod<?> method : ctType.getMethods()) {
                if (method.isPrivate()) {
                    for (MethodCallState state : search.getInvocationsOfMethod()) {
                        if (state.getMethod() != null
                                && state.getMethod().equals(method)
                                && state.checkCallState()) {
                            if (refs.stream()
                                    .filter(v -> v.getExecutableDeclaration() != null)
                                    .noneMatch(
                                            v ->
                                                    method.getSimpleName()
                                                            .equals(
                                                                    v.getExecutableDeclaration()
                                                                            .getSimpleName()))) {
                                removable.add(method);
                            }
                        }
                    }
                }
            }
        }
        String title = "Speculative Generality";
        StringBuilder description = new StringBuilder();
        description
                .append(String.format("%s unused elements found", removable.size()))
                .append("\n\n");
        for (CtElement ctElement : removable) {
            description.append(ctElement.getPath() + "\n");
        }
        return new Report(title, description.toString(), !removable.isEmpty(), removable.size());
    }
}

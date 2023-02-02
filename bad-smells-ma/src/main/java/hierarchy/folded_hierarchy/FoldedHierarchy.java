package hierarchy.folded_hierarchy;

import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.AbstractAnalyzer;
import edu.kit.kastel.sdq.case4lang.refactorlizar.analyzer.api.Report;
import edu.kit.kastel.sdq.case4lang.refactorlizar.commons.Settings;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.ModularLanguage;
import edu.kit.kastel.sdq.case4lang.refactorlizar.model.SimulatorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtType;

public class FoldedHierarchy extends AbstractAnalyzer {

    @Override
    public Settings getSettings() {
        return new Settings.SettingsBuilder()
                .addSetting("layer", "name of layers seperated by `,`")
                .build();
    }

    @Override
    protected void checkSettings(Settings settings) {}

    @Override
    protected Report fullAnalysis(
            ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        /**
         * Hier ist das Problem das fehlende Layer für eine Klasse sich eigetnlich nur über die
         * Vererbung feststellen lassen. Die Indentifikation ob eine Methode Layer A oder B ist, ist
         * quasi unmöglich.
         */
        List<String> layers =
                Arrays.asList(settings.getSetting("layer").orElseThrow().getValue().split(","));
        var types =
                simulatorModel.getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());

        types.removeIf(
                v ->
                        v.getSuperclass() != null
                                && types.contains(v.getSuperclass().getTypeDeclaration()));
        types.removeIf(v -> v.isAbstract());
        types.removeIf(v -> v.isInterface());
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (CtType<?> ctType : types) {
            List<CtType<?>> parents = new ArrayList<>();
            parents.add(ctType);
            CtType<?> start = ctType;
            while (start.getSuperclass() != null
                    && simulatorModel.getTypeWithQualifiedName(
                                    start.getSuperclass().getQualifiedName())
                            != null) {
                start = start.getSuperclass().getTypeDeclaration();
                parents.add(start);
            }
            if (ctType.getReferencedTypes().stream()
                    .filter(
                            v ->
                                    v.getTypeDeclaration() != null
                                            && v.getTypeDeclaration()
                                                    .getPosition()
                                                    .isValidPosition())
                    .noneMatch(
                            v ->
                                    layers.stream()
                                            .anyMatch(
                                                    layer ->
                                                            v.getTypeDeclaration()
                                                                    .getPosition()
                                                                    .getFile()
                                                                    .getAbsolutePath()
                                                                    .contains(layer)))) {
                continue;
            }
            for (String layer : layers) {
                if (parents.stream()
                        .noneMatch(
                                v -> v.getPosition().getFile().getAbsolutePath().contains(layer))) {
                    count++;
                    sb.append("Type:" + ctType.getQualifiedName())
                            .append(" and it's parent do not have a " + layer + " layer")
                            .append("\n");
                }
            }
        }
        return new Report("Folded Hierarchy", sb.toString(), sb.toString().isBlank(), count);
        // FIXME: Wir fordern das die Klassen einem Namensschema folgen. Ergebnis Type A hat m statt
        // n layern(m<n).
    }

    List<String> findNormalLayerViolation() {
        return null;
    }

    Report old(ModularLanguage language, SimulatorModel simulatorModel, Settings settings) {
        /**
         * Hier ist das Problem das fehlende Layer für eine Klasse sich eigetnlich nur über die
         * Vererbung feststellen lassen. Die Indentifikation ob eine Methode Layer A oder B ist, ist
         * quasi unmöglich.
         */
        List<String> layers =
                Arrays.asList(settings.getSetting("layer").orElseThrow().getValue().split(","));
        var types =
                simulatorModel.getComponents().stream()
                        .flatMap(v -> v.getTypes().stream())
                        .collect(Collectors.toSet());

        types.removeIf(
                v ->
                        v.getSuperclass() != null
                                && types.contains(v.getSuperclass().getTypeDeclaration()));
        types.removeIf(v -> v.isAbstract());
        types.removeIf(v -> v.isInterface());
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (CtType<?> ctType : types) {
            List<CtType<?>> parents = new ArrayList<>();
            parents.add(ctType);
            CtType<?> start = ctType;
            while (start.getSuperclass() != null
                    && simulatorModel.getTypeWithQualifiedName(
                                    start.getSuperclass().getQualifiedName())
                            != null) {
                start = start.getSuperclass().getTypeDeclaration();
                parents.add(start);
            }
            if (ctType.getReferencedTypes().stream()
                    .filter(
                            v ->
                                    v.getTypeDeclaration() != null
                                            && v.getTypeDeclaration()
                                                    .getPosition()
                                                    .isValidPosition())
                    .noneMatch(
                            v ->
                                    layers.stream()
                                            .anyMatch(
                                                    layer ->
                                                            v.getTypeDeclaration()
                                                                    .getPosition()
                                                                    .getFile()
                                                                    .getAbsolutePath()
                                                                    .contains(layer)))) {
                continue;
            }
            for (String layer : layers) {
                if (parents.stream()
                        .noneMatch(
                                v -> v.getPosition().getFile().getAbsolutePath().contains(layer))) {
                    count++;
                    sb.append("Type:" + ctType.getQualifiedName())
                            .append(" and it's parent do not have a " + layer + " layer")
                            .append("\n");
                }
            }
        }
        return new Report("Folded Hierarchy", sb.toString(), sb.toString().isBlank(), count);
        // FIXME: Wir fordern das die Klassen einem Namensschema folgen. Ergebnis Type A hat m statt
        // n layern(m<n).
    }
}

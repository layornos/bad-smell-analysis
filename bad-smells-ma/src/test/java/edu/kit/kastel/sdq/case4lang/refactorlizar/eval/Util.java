package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mosim.refactorlizar.architecture.evaluation.Application;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.Result;

public class Util {
  
  @Test
  void deleteAllFileExpect() throws IOException {
    Path path = Path.of(
        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/camunda/mCamunda/engine");
    Set<String> fileNames = new HashSet<>();

    String badSmell = "deficient_encapsulation";
    String effectedType = "ProcessApplicationDeploymentBuilderImpl";


    fileNames.add(effectedType);
    
    Path target = Path.of("Camunda", badSmell, effectedType);
    FileUtils.copyDirectory(path.toFile(), Path.of(target.toString(), "before").toFile());
    Files.walk(target).filter(v -> !Files.isDirectory(v))
        .filter(v -> !v.getFileName().endsWith(".java"))
        .filter(v -> !fileNames.contains(v.getFileName().toString().replaceAll(".java", "")))
        .forEach(v -> {
          try {
            Files.delete(v);
          } catch (Exception e) {
          }
        });
  }

  @Test
  void metrics() throws IOException {
    Path path = Path.of(
        "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/simulizar/SimuLizar");
        Files.createFile(Path.of("metrics_simulizar"));
        Files.list(path).forEach(badSmellRoot -> {
          // System.out.println(badSmellRoot);
          StringBuilder builder = new StringBuilder();
          builder.append("% " + badSmellRoot.getFileName().toString()).append("\n");
          try {
            Files.list(badSmellRoot).forEach(effectedTypeRoot -> {
             Path input = Path.of(effectedTypeRoot.toString(), "before");
              Application application = new Application();
              Result result = application.evaluate(CalculationMode.NO_OFFSET, input.toString());
              DecimalFormat df = new DecimalFormat("#.###");
              DecimalFormat cf = new DecimalFormat("#.####");
              builder.append("  " + effectedTypeRoot.getFileName().toString());
              builder.append(" & ");
              builder.append(df.format(result.getComplexity().getValue()));
              builder.append(" & ");
              builder.append(cf.format(result.getCohesion().getValue()));
              builder.append(" & ");
              builder.append(df.format(result.getCoupling().getValue()));
              builder.append(" \\\\");
              builder.append("\n");
              System.out.println(builder.toString());
              System.out.println("\n\n");
            });
              try {
              Files.writeString(Path.of("metrics_simulizar"), builder.toString(),
                  StandardOpenOption.APPEND);
            } catch (IOException e) {
              e.printStackTrace();
            }
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        });
    }
}

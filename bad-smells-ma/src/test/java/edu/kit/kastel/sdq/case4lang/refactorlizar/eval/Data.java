package edu.kit.kastel.sdq.case4lang.refactorlizar.eval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mosim.refactorlizar.architecture.evaluation.Application;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.Result;

public class Data {
  
  public static void main(String[] args) throws IOException {
    new Data().go();
  }
  private String badSmell = "Speculative Generality";

  private String path =
      "C:/Users/Martin Wittlinger/OneDrive - bwedu/Uni zeugs/Masterarbeit/Projekte/eval/%s/historicalScenarios/%s";
  private List<String> historicalScenarios = new ArrayList<>();
  {
    {
      historicalScenarios.add("smartGrid");
      historicalScenarios.add("KAMP");
      historicalScenarios.add("camunda");
      historicalScenarios.add("simulizar");
    }
  }
  private String caseStudy = historicalScenarios.get(1);
  public void go() throws IOException {
    Application application = new Application();
    Container before = new Container();
    Container after = new Container();
    StringBuilder sb = new StringBuilder();
      String path = String.format(this.path, caseStudy, badSmell);
      Files.list(Path.of(path)).forEach(v -> {
        if (Path.of(v.toString(), "after").toFile().exists()) {

          String fileName = v.getFileName().toString();
          System.out.println(fileName + "started");

          String beforePath = Path.of(v.toString(), "before").toString();
          String afterPath = Path.of(v.toString(), "after").toString();

          System.out.println(fileName + "before started");
          Result beforeResults = application.evaluate(CalculationMode.ONE_OFFSET, beforePath);
          System.out.println(fileName + "before finished");
          System.out.println(fileName + "after started");
          Result afterResults = null;
          Path dataClasses = Path.of(afterPath, "DataClasses");
          if (Files.exists(dataClasses)) {
            System.out.println("found dataclasses file");
            afterResults = application.evaluate(CalculationMode.ONE_OFFSET, dataClasses.toString(), "",
                List.of(afterPath).toArray(new String[0]));
          }
          else {
            afterResults = application.evaluate(CalculationMode.ONE_OFFSET, afterPath);
          }
          if (beforeResults.getSizeOfSystem().getValue() < afterResults.getSizeOfSystem().getValue()) {
            System.out.println("help");
          }
          System.out.println(fileName + "after finished");
          before.pushResult(beforeResults);
          after.pushResult(afterResults);
          System.out.println(fileName + "finished");
        }
      });
      sb.append(IntStream.range(1, after.cohesion.size() + 1).boxed().sorted().map(v -> v.toString())
          .collect(Collectors.joining(";")));
      sb.append("\n");
      sb.append(toString(before.complexity));
      sb.append(toString(before.coupling));
      sb.append(toString(before.cohesion));
      sb.append(toString(after.complexity));
      sb.append(toString(after.coupling));
      sb.append(toString(after.cohesion));
      System.out.println(sb.toString());
      Files.writeString(Path.of(badSmell+"_"+caseStudy+".csv"), sb.toString());
    }

  private class Container {

    private LinkedList<String> complexity = new LinkedList<>();
    private LinkedList<String> cohesion = new LinkedList<>();
    private LinkedList<String> coupling = new LinkedList<>();

    void pushResult(Result result) {
      complexity.add(String.valueOf(Math.abs(result.getComplexity().getValue())));
      cohesion.add(String.valueOf(Math.abs(result.getCohesion().getValue())));
      coupling.add(String.valueOf(Math.abs(result.getCoupling().getValue())));
      // i have zero clue why there are negative numbers.
      // But neither time nor motivation to find the reason
    }
  }

  private String toString(List<String> input) {
    StringBuilder sb = new StringBuilder();
    sb.append(input.stream().collect(Collectors.joining(";"))).append("\n");
    return sb.toString();
  }
}

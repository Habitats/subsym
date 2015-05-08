package subsym.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.Log;
import subsym.flatland.FlatlandAnnSimulator;
import subsym.beertracker.BeerTracker;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.gui.GeneticGui;
import subsym.genetics.gui.GeneticGuiListener;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.SigmaScaled;
import subsym.genetics.matingselection.Tournament;
import subsym.lolz.Lolz;
import subsym.onemax.OneMax;
import subsym.surprisingsequence.SurprisingSequences;

/**
 * Created by Patrick on 03.03.2015.
 */
public class Genetics implements GeneticGuiListener {

  private static String TAG = Genetics.class.getSimpleName();
  private final GeneticGui gui;
  private GeneticProblem lastSolution;

  public Genetics() {
    gui = new GeneticGui();
    gui.setListener(this);
//    gui.setPreferences(GeneticPreferences.getAiLife());
  }

  private static void lolz() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .000001;
    double populationMutationRate = 0.2;
    double crossOverRate = 1;
    GeneticPreferences prefs = new GeneticPreferences(20, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.2), new SigmaScaled(), Integer.MAX_VALUE);
    Log.v(TAG, GeneticEngine.solve(new Lolz(prefs, prefs.getBitVectorSize(), prefs.getZeroThreashold()), true));

//    averageOver(genotypeMutationRate, populationMutationRate, crossOverRate, 1000);
  }

  private static void oneMax() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .0001;
    double populationMutationRate = 1;
    double crossOverRate = 0.5;
    GeneticRun run = new GeneticRun();
    Tournament mateSelectionMode = new Tournament(10, 0.05);
    GeneticPreferences prefs = new GeneticPreferences(40, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.5), mateSelectionMode, Integer.MAX_VALUE);
    IntStream.range(0, 1).forEach(i -> run.add(GeneticEngine.solve(new OneMax(prefs, 10000), true)));

    Log.v(TAG, run.getBest());
  }

  private static void surprisingSequence() {
//    profileSurprisingSequence();
    profileSingleSurprisingSequence();
  }

  private static void profileSingleSurprisingSequence() {
    double crossOverRate = .9;
    double genomeMutationRate = .010;
    double populationMutationRate = .9;
    AdultSelection adultSelectionMode = new Mixing(0.5);
    MatingSelection mateSelectionMode = new Tournament(10, 0.05);
    int alphabetSize = 40;
    int populationSize = 40;
    GeneticPreferences prefs = new GeneticPreferences(populationSize, crossOverRate, populationMutationRate, //
                                                      genomeMutationRate, adultSelectionMode, mateSelectionMode, Integer.MAX_VALUE);
    for (int length = 90; length < alphabetSize * 3; length++) {
      SurprisingSequences problem = new SurprisingSequences(prefs, alphabetSize, length, true);
      GeneticProblem solution = GeneticEngine.solve(problem, true);
      Log.v(TAG, solution);
    }
  }

  @Override
  public void plotMultipleToggled(boolean selected) {
    Log.i(TAG, "Multiple plot toggled: " + (selected ? "on" : "off"));
    if (selected) {
      gui.getPlot().setMultipleRunsDataset();
    } else {
      gui.getPlot().setSingleRunDataset();
    }
  }

  @Override
  public void run(GeneticPreferences prefs) {
    Log.i(TAG, "Running ...");
    int runCount = prefs.getRunCount();
    GeneticRun runs = new GeneticRun();
    prefs.reset();

    IntStream.range(0, runCount).forEach(i -> {
      GeneticProblem problem = prefs.getPuzzleCopy();
      if (prefs.shouldIncrement()) {
        prefs.increment();
        problem.increment(prefs.getIncrement());
      }
      problem.setPlotter(gui.getPlot());
      runs.add(problem);
    });

    GeneticEngine.solveInBackground(runs, prefs.isLoggingEnabled(), this);
  }

  @Override
  public void stop() {
    Log.i(TAG, "Terminating ...");
    GeneticEngine.kill();
  }

  public void onSolved(GeneticProblem solution) {
    lastSolution = solution;
    solution.onSolved();
  }

  public void clear() {
    gui.clear();
  }

  public void onSolved(GeneticRun runs) {
    if (runs.size() > 1) {
      Log.i(TAG, runs.getAverage());
    }
    GeneticPreferences prefs = runs.getPreferences();
    if (prefs.shouldPlotMultiple()) {
      gui.getPlot().addMultipleRunsValue("cr", runs.getAverageGeneration(), prefs.getCrossOverRate());
      gui.getPlot().addMultipleRunsValue("pmr", runs.getAverageGeneration(), prefs.getPopulationMutationRate());
      gui.getPlot().addMultipleRunsValue("mr", runs.getAverageGeneration(), prefs.getGenomeMutationRate() * 100);
    } else {
      if (runs.size() > 1) {
        gui.getPlot().plotAverage();
      }
    }
  }

  public static class GeneticRun {

    private List<GeneticProblem> runs = new ArrayList<>();

    public void add(GeneticProblem solution) {
      runs.add(solution);
    }

    public String getBest() {
      Map<String, Double> avgMap = runs.stream()//
          .map(p -> p.getId()).distinct() //
          .collect(Collectors.toMap(p -> p, p -> runs.stream() //
              .filter(p2 -> p.equals(p2.getId())) //
              .mapToDouble(p2 -> p2.generations()) //
              .average().getAsDouble()));
      Double bestAvg = avgMap.values().stream().min(Comparator.<Double>naturalOrder()).get();
      Predicate<String> isBest = p -> avgMap.get(p) == bestAvg;
      String best = "Best Average: " + bestAvg + " - " + avgMap.keySet().stream().filter(isBest).findFirst().get();

      return best;
    }

    public String getAverage() {
      double avg = getAverageGeneration();
      List<Double> generations = runs.stream().map(i -> (double) i.generations()).collect(Collectors.toList());
      double sd = Population.standardDeviation(generations);

      return String.format("Average: %.2f - Standard Deviation: %.2f - Normalized Standard Deviation: %.2f", avg, sd,
                           1 - Math.abs((avg - sd) / avg));
    }

    private double getAverageGeneration() {
      return runs.stream().mapToInt(GeneticProblem::generations).average().getAsDouble();
    }

    public int size() {
      return runs.size();
    }

    public Stream<GeneticProblem> stream() {
      return runs.stream();
    }

    public GeneticPreferences getPreferences() {
      return runs.get(0).getPreferences();
    }
  }

  public static List<String> values() {
    return Arrays.asList(BeerTracker.class.getSimpleName(), SurprisingSequences.class.getSimpleName(), Lolz.class.getSimpleName(),
                         OneMax.class.getSimpleName(), FlatlandAnnSimulator.class.getSimpleName());
  }

  @Override
  public void runBenchmark(GeneticPreferences prefs) {
    double i = 0.001;
//      prefs.setGenomeMutationRate(i);
    run(GeneticPreferences.copy(prefs));
//      i += 0.001;
  }

  @Override
  public void demo(GeneticPreferences prefs) {
    if (lastSolution != null) {
      lastSolution.demo(prefs);
    } else {
      prefs.getPuzzleCopy().demo(prefs);
    }
  }
}

package subsym.genetics.gui;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

import subsym.Log;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.genetics.Genetics;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.FullTurnover;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.adultselection.OverProduction;
import subsym.genetics.matingselection.Boltzman;
import subsym.genetics.matingselection.FitnessProportiate;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.Rank;
import subsym.genetics.matingselection.SigmaScaled;
import subsym.genetics.matingselection.Tournament;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIComboBox;
import subsym.gui.AIContiniousScrollPane;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.Plot;
import subsym.lolz.Lolz;
import subsym.onemax.OneMax;
import subsym.surprisingsequence.SurprisingSequences;

/**
 * Created by Patrick on 03.03.2015.
 */
public class GeneticGui extends AIGui {

  private static final String TAG = GeneticGui.class.getSimpleName();

  private Plot plot;
  private JPanel mainPanel;
  private AITextArea inputField;
  private GeneticPreferences prefs;
  private GeneticGuiListener listener;
  private AIContiniousScrollPane logField;

  private AISlider crossoverSlider;
  private AISlider genomeMutationSlider;
  private AISlider populationMutationSlider;

  private AILabel populationMutationField;
  private AILabel crossoverField;
  private AILabel genomeMutationField;
  private AILabel bitVectorSizeLabel;
  private AILabel alphabetSizeLabel;
  private AILabel surprisingLengthLabel;
  private AILabel zeroThresholdLabel;
  private AILabel mixingRateLabel;
  private AILabel tournamentLabel;
  private AILabel overProductionLabel;
  private AILabel genomeMutationValField;
  private AILabel populationMutationValField;
  private AILabel populationSize;
  private AILabel presetsLabel;

  private AIButton runButton;
  private AIButton stopButton;

  private JCheckBox globalCheckBox;
  private JCheckBox enableLoggingCheckbox;

  private JTextField crossoverInput;
  private JTextField tournamentInput;
  private JTextField mixingRateInput;
  private JTextField alphabetSizeInput;
  private JTextField bitVectorSizeInput;
  private JTextField zeroThresholdInput;
  private JTextField populationSizeInput;
  private JTextField overProductionInput;
  private JTextField genomeMutationInput;
  private JTextField surprisingLengthInput;
  private JTextField populationMutationInput;

  private AIComboBox puzzleSelect;
  private AIComboBox adultSelection;
  private AIComboBox matingSelection;
  private AIComboBox presetsComboBox;
  private JTextField runCountInput;
  private AILabel runCountLabel;
  private JCheckBox incrementingCheckBox;
  private JCheckBox plotMultipleCheckbox;
  private AIButton benchMarkButton;

  public GeneticGui() {
    prefs = GeneticPreferences.getDefault();

    Genetics.values().forEach(puzzleSelect::addItem);
    AdultSelection.values().forEach(adultSelection::addItem);
    MatingSelection.values().forEach(matingSelection::addItem);
    GeneticPreferences.getPresets().keySet().forEach(presetsComboBox::addItem);

    presetsComboBox.addActionListener(
        e -> setPreferences(GeneticPreferences.getPresets().get(((JComboBox) e.getSource()).getSelectedItem())));

    matingSelection.addActionListener(e -> updatePreferences());
    adultSelection.addActionListener(e -> updatePreferences());
    puzzleSelect.addActionListener(e -> updatePreferences());

    crossoverSlider.setMaximum((int) getSliderRes());
    crossoverSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      if (value > 0) {
        crossoverInput.setText(String.valueOf(value));
      }
      updatePreferences();
    });
    genomeMutationSlider.setMaximum((int) getSliderRes());
    genomeMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      if (value > 0) {
        genomeMutationInput.setText(String.valueOf(value));
      }
      updatePreferences();
    });
    populationMutationSlider.setMaximum((int) getSliderRes());
    populationMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      if (value > 0) {
        populationMutationInput.setText(String.valueOf(value));
      }
      updatePreferences();
    });

    runButton.addActionListener(e -> run());
    stopButton.addActionListener(e -> listener.stop());
    benchMarkButton.addActionListener(e -> benchmark());

    crossoverInput.addActionListener(e -> updatePreferences());
    mixingRateInput.addActionListener(e -> updatePreferences());
    tournamentInput.addActionListener(e -> updatePreferences());
    alphabetSizeInput.addActionListener(e -> updatePreferences());
    bitVectorSizeInput.addActionListener(e -> updatePreferences());
    zeroThresholdInput.addActionListener(e -> updatePreferences());
    populationSizeInput.addActionListener(e -> updatePreferences());
    genomeMutationInput.addActionListener(e -> updatePreferences());
    surprisingLengthInput.addActionListener(e -> updatePreferences());
    populationMutationInput.addActionListener(e -> updatePreferences());

    logField.setFont(Font.decode(Font.MONOSPACED));

    buildFrame(mainPanel, logField, null);
    mainPanel.requestFocus();
    mainPanel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
          run();
        }
      }
    });
    logField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          logField.transferFocus();
        }
      }
    });

    adultSelection.setSelectedItem(Mixing.class.getSimpleName());
    matingSelection.setSelectedItem(Tournament.class.getSimpleName());
    puzzleSelect.setSelectedItem(SurprisingSequences.class.getSimpleName());

    enableLoggingCheckbox.addActionListener(e -> updatePreferences());
    plotMultipleCheckbox.addActionListener(e -> {
      updatePreferences();
      listener.plotMultipleToggled(plotMultipleCheckbox.isSelected());
    });

    updatePreferences();
  }

  private void run() {
    if (updatePreferences()) {
      listener.run(prefs);
    }
  }
  private void benchmark(){
    if (updatePreferences()) {
      listener.runBenchmark(prefs);
    }
  }

  private void setVisibleLolz(boolean b) {
    zeroThresholdLabel.setVisible(b);
    zeroThresholdInput.setVisible(b);
    bitVectorSizeInput.setVisible(b);
    bitVectorSizeLabel.setVisible(b);
    surprisingLengthInput.setVisible(!b);
    surprisingLengthLabel.setVisible(!b);
    alphabetSizeInput.setVisible(!b);
    alphabetSizeLabel.setVisible(!b);
    globalCheckBox.setVisible(!b);
  }

  private void setVisibleOneMax(boolean b) {
    zeroThresholdLabel.setVisible(!b);
    zeroThresholdInput.setVisible(!b);
    bitVectorSizeInput.setVisible(b);
    bitVectorSizeLabel.setVisible(b);
    surprisingLengthInput.setVisible(!b);
    surprisingLengthLabel.setVisible(!b);
    alphabetSizeInput.setVisible(!b);
    alphabetSizeLabel.setVisible(!b);
    globalCheckBox.setVisible(!b);
  }

  private void setVisibleSurprising(boolean b) {
    zeroThresholdLabel.setVisible(!b);
    zeroThresholdInput.setVisible(!b);
    bitVectorSizeInput.setVisible(!b);
    bitVectorSizeLabel.setVisible(!b);
    surprisingLengthInput.setVisible(b);
    surprisingLengthLabel.setVisible(b);
    alphabetSizeInput.setVisible(b);
    alphabetSizeLabel.setVisible(b);
    globalCheckBox.setVisible(b);
  }

  private void initDefaultPreferences() {
    adultSelection.setSelectedItem(prefs.getAdultSelectionMode().getClass().getSimpleName());
    matingSelection.setSelectedItem(prefs.getMateSelectionMode().getClass().getSimpleName());

    crossoverInput.setText(String.valueOf(prefs.getCrossOverRate()));
    genomeMutationInput.setText(String.valueOf(prefs.getGenomeMutationRate()));
    populationMutationInput.setText(String.valueOf(prefs.getPopulationMutationRate()));

    crossoverSlider.setValue((int) (prefs.getCrossOverRate() * getSliderRes()));
    genomeMutationSlider.setValue((int) (prefs.getGenomeMutationRate() * getSliderRes()));
    populationMutationSlider.setValue((int) (prefs.getPopulationMutationRate() * getSliderRes()));

    populationSizeInput.setText(String.valueOf(prefs.getPopulationSize()));
  }

  public void setListener(GeneticGuiListener listener) {
    this.listener = listener;
  }

  private void setTournamentVisible(boolean b) {
    tournamentInput.setVisible(b);
    tournamentLabel.setVisible(b);
  }

  private void setMixingVisible(boolean b) {
    mixingRateInput.setVisible(b);
    mixingRateLabel.setVisible(b);
  }

  private void setOverProductionVisible(boolean b) {
    overProductionInput.setVisible(b);
    overProductionLabel.setVisible(b);
  }

  public boolean updatePreferences() {
    Log.i(TAG, "Updating preferences ...");
    try {
      prefs.setCrossOverRate(Double.parseDouble(crossoverInput.getText()));
      prefs.setGenomeMutationRate(Double.parseDouble(genomeMutationInput.getText()));
      prefs.setPopulationMutationRate(Double.parseDouble(populationMutationInput.getText()));

      crossoverSlider.setValue((int) (Double.parseDouble(crossoverInput.getText()) * getSliderRes()));
      genomeMutationSlider.setValue((int) (Double.parseDouble(genomeMutationInput.getText()) * getSliderRes()));
      populationMutationSlider.setValue((int) (Double.parseDouble(populationMutationInput.getText()) * getSliderRes()));

      prefs.setAlphabetSize(Integer.parseInt(alphabetSizeInput.getText()));
      prefs.setBitVectorSize(Integer.parseInt(bitVectorSizeInput.getText()));
      prefs.setPopulationSize(Integer.parseInt(populationSizeInput.getText()));
      prefs.setSurprisingLength(Integer.parseInt(surprisingLengthInput.getText()));

      prefs.setPuzzle(getPuzzle());
      prefs.setMateSelectionMode(getMatingSelection());
      prefs.setAdultSelectionMode(getAdultSelection());

      prefs.setRunCount(Integer.parseInt(runCountInput.getText()));
      prefs.setShouldIncrement(incrementingCheckBox.isSelected());
      prefs.setPlotMultiple(plotMultipleCheckbox.isSelected());

      prefs.logginEnabled(enableLoggingCheckbox.isSelected());
    } catch (NumberFormatException e) {
      Log.i(TAG, "Invalid values in preferences!");
      return false;
    }
    return true;
  }

  private GeneticProblem getPuzzle() {
    String puzzle = String.valueOf(puzzleSelect.getSelectedItem());
    if (puzzle.equals(SurprisingSequences.class.getSimpleName())) {
      setVisibleSurprising(true);
      return new SurprisingSequences(prefs, Integer.parseInt(alphabetSizeInput.getText()),
                                     Integer.parseInt(surprisingLengthInput.getText()), globalCheckBox.isSelected());
    } else if (puzzle.equals(Lolz.class.getSimpleName())) {
      setVisibleLolz(true);
      return new Lolz(prefs, Integer.parseInt(bitVectorSizeInput.getText()),
                      Integer.parseInt(zeroThresholdInput.getText()));
    } else if (puzzle.equals(OneMax.class.getSimpleName())) {
      setVisibleOneMax(true);
      return new OneMax(prefs, Integer.parseInt(bitVectorSizeInput.getText()));
    }
    throw new IllegalStateException("No puzzle selected!");
  }

  private AdultSelection getAdultSelection() {
    String mode = String.valueOf(adultSelection.getSelectedItem());
    if (mode.equals(FullTurnover.class.getSimpleName())) {
      setMixingVisible(false);
      setOverProductionVisible(false);
      return new FullTurnover();
    } else if (mode.equals(Mixing.class.getSimpleName())) {
      setMixingVisible(true);
      setOverProductionVisible(false);
      return new Mixing(Double.parseDouble(mixingRateInput.getText()));
    } else if (mode.equals(OverProduction.class.getSimpleName())) {
      setMixingVisible(false);
      setOverProductionVisible(true);
      return new OverProduction(Double.parseDouble(overProductionInput.getText()));
    }
    throw new IllegalStateException("No AdultSelection");
  }

  private MatingSelection getMatingSelection() {
    String mode = String.valueOf(matingSelection.getSelectedItem());
    if (mode.equals(FitnessProportiate.class.getSimpleName())) {
      setTournamentVisible(false);
      return new FitnessProportiate();
    } else if (mode.equals(SigmaScaled.class.getSimpleName())) {
      setTournamentVisible(false);
      return new SigmaScaled();
    } else if (mode.equals(Boltzman.class.getSimpleName())) {
      setTournamentVisible(false);
      return new Boltzman();
    } else if (mode.equals(Rank.class.getSimpleName())) {
      setTournamentVisible(false);
      return new Rank();
    } else if (mode.equals(Tournament.class.getSimpleName())) {
      setTournamentVisible(true);
      String[] split = tournamentInput.getText().split("/");
      return new Tournament(Integer.parseInt(split[0]), Double.parseDouble(split[1]));
    }
    throw new IllegalStateException("No MatingSelection");
  }

  @Override
  protected int getDefaultCloseOperation() {
    return WindowConstants.EXIT_ON_CLOSE;
  }

  @Override
  protected Dimension getPreferredSize() {
    return new Dimension(1500, 1200);
  }

  @Override
  public JPanel getMainPanel() {
    return mainPanel;
  }

  @Override
  public AICanvas getDrawingCanvas() {
    throw new NotImplementedException();
  }

  @Override
  public AITextArea getInputField() {
    return inputField;
  }

  public void clear() {
    plot.clear();
  }

  public Plot getPlot() {
    return plot;
  }

  private double getSliderRes() {
    return 1000.;
  }

  public void setPreferences(GeneticPreferences prefs) {
    crossoverInput.setText(String.valueOf(prefs.getCrossOverRate()));
    populationSizeInput.setText(String.valueOf(prefs.getPopulationSize()));
    genomeMutationInput.setText(String.valueOf(prefs.getGenomeMutationRate()));
    populationMutationInput.setText(String.valueOf(prefs.getPopulationMutationRate()));

    puzzleSelect.setSelectedItem(prefs.getPuzzle().getClass().getSimpleName());
    adultSelection.setSelectedItem(prefs.getAdultSelectionMode().getClass().getSimpleName());
    matingSelection.setSelectedItem(prefs.getMateSelectionMode().getClass().getSimpleName());

    incrementingCheckBox.setSelected(prefs.shouldIncrement());

    if (prefs.getAdultSelectionMode() instanceof OverProduction) {
      overProductionInput
          .setText(String.valueOf(((OverProduction) prefs.getAdultSelectionMode()).getOverProductionRate()));
    } else if (prefs.getAdultSelectionMode() instanceof Mixing) {
      mixingRateInput.setText(String.valueOf(((Mixing) prefs.getAdultSelectionMode()).getMixingRate()));
    }

    if (prefs.getPuzzle() instanceof SurprisingSequences) {
      SurprisingSequences puzzle = (SurprisingSequences) prefs.getPuzzle();
      surprisingLengthInput.setText(String.valueOf(puzzle.getSurprisingLength()));
      alphabetSizeInput.setText(String.valueOf(puzzle.getAlphabetSize()));
    } else if (prefs.getPuzzle() instanceof OneMax) {
      bitVectorSizeInput.setText(String.valueOf(((OneMax) prefs.getPuzzle()).getBitVecotorSize()));
    } else if (prefs.getPuzzle() instanceof Lolz) {
      Lolz puzzle = (Lolz) prefs.getPuzzle();
      zeroThresholdInput.setText(String.valueOf(puzzle.getZeroThreshold()));
      bitVectorSizeInput.setText(String.valueOf(puzzle.getBitVecotorSize()));
    }

    if (prefs.getMateSelectionMode() instanceof Tournament) {
      Tournament mateSelectionMode = (Tournament) prefs.getMateSelectionMode();
      tournamentInput.setText(mateSelectionMode.getTournamentK() + "/" + mateSelectionMode.getTournamentE());
    }
  }
}

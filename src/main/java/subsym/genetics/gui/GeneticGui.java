package subsym.genetics.gui;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

import javax.swing.*;

import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.genetics.Genetics;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.FullTurnover;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.adultselection.OverProduction;
import subsym.genetics.matingselection.FitnessProportiate;
import subsym.genetics.matingselection.MatingSelection;
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

  private GeneticGuiListener listener;
  private JPanel mainPanel;
  private AISlider genomeMutationSlider;
  private AISlider populationMutationSlider;
  private AISlider crossoverSlider;
  private JTextField genomeMutationInput;
  private JTextField populationMutationInput;
  private JTextField crossoverInput;
  private Plot plot;
  private AITextArea inputField;
  private AIContiniousScrollPane logField;
  private AIComboBox adultSelection;
  private AIComboBox matingSelection;
  private AILabel populationSize;
  private JTextField populationSizeInput;
  private AIButton runButton;
  private AILabel populationMutationField;
  private AILabel crossoverField;
  private AILabel genomeMutationField;
  private AIButton stopButton;
  private AIComboBox puzzleSelect;
  private AILabel bitVectorSizeLabel;
  private JTextField bitVectorSizeInput;
  private JTextField alphabetSizeInput;
  private AILabel alphabetSizeLabel;
  private AILabel surprisingLengthLabel;
  private JTextField surprisingLengthInput;
  private AILabel zeroThresholdLabel;
  private JTextField zeroThresholdInput;
  private JTextField mixingRateInput;
  private AILabel mixingRateLabel;
  private AILabel tournamentLabel;
  private JTextField tournamentInput;
  private AILabel overProductionLabel;
  private JTextField overProductionInput;
  private AILabel genomeMutationValField;
  private AILabel populationMutationValField;
  private GeneticPreferences prefs;

  public GeneticGui() {
    prefs = GeneticPreferences.getDefault();

    Genetics.values().forEach(puzzleSelect::addItem);
    AdultSelection.values().forEach(adultSelection::addItem);
    MatingSelection.values().forEach(matingSelection::addItem);

    matingSelection.addActionListener(e -> updatePreferences());
    adultSelection.addActionListener(e -> updatePreferences());
    puzzleSelect.addActionListener(e -> updatePreferences());

    crossoverSlider.setMaximum((int) getSliderRes());
    crossoverSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      crossoverInput.setText(String.valueOf(value));
      updatePreferences();
    });
    genomeMutationSlider.setMaximum((int) getSliderRes());
    genomeMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      genomeMutationInput.setText(String.valueOf(value));
      updatePreferences();
    });
    populationMutationSlider.setMaximum((int) getSliderRes());
    populationMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / getSliderRes();
      populationMutationInput.setText(String.valueOf(value));
      updatePreferences();
    });

    runButton.addActionListener(e -> run());
    stopButton.addActionListener(e -> listener.stop());

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

    adultSelection.setSelectedItem(Mixing.class.getSimpleName());
    matingSelection.setSelectedItem(Tournament.class.getSimpleName());
    puzzleSelect.setSelectedItem(SurprisingSequences.class.getSimpleName());

    updatePreferences();
  }

  private void run() {
    updatePreferences();
    listener.run(prefs);
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

  public void updatePreferences() {
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
  }

  private GeneticProblem getPuzzle() {
    String puzzle = String.valueOf(puzzleSelect.getSelectedItem());
    if (puzzle.equals(SurprisingSequences.class.getSimpleName())) {
      setVisibleLolz(false);
      setVisibleOneMax(false);
      setVisibleSurprising(true);
      return new SurprisingSequences(prefs, Integer.parseInt(alphabetSizeInput.getText()),
                                     Integer.parseInt(surprisingLengthInput.getText()));
    } else if (puzzle.equals(Lolz.class.getSimpleName())) {
      setVisibleLolz(true);
      setVisibleOneMax(false);
      setVisibleSurprising(false);
      return new Lolz(prefs, Integer.parseInt(bitVectorSizeInput.getText()),
                      Integer.parseInt(zeroThresholdInput.getText()));
    } else if (puzzle.equals(OneMax.class.getSimpleName())) {
      setVisibleLolz(false);
      setVisibleOneMax(true);
      setVisibleSurprising(false);
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
    return 100.;
  }

}

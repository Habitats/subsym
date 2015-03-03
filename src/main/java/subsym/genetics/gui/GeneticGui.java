package subsym.genetics.gui;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import subsym.genetics.GeneticPreferences;
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
import subsym.gui.AITextField;
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
  private subsym.gui.AIComboBox adultSelection;
  private AIComboBox matingSelection;
  private AILabel populationSize;
  private JTextField populationField;
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
  private AILabel genomeMutationValField;
  private AILabel populationMutationValField;
  private GeneticPreferences prefs;

  public GeneticGui() {
    prefs = GeneticPreferences.getDefault();

    AdultSelection.values().forEach(adultSelection::addItem);
    MatingSelection.values().forEach(matingSelection::addItem);
    Genetics.values().forEach(puzzleSelect::addItem);
    matingSelection
        .addActionListener(e -> matingModeSelected(((JComboBox) e.getSource()).getSelectedItem().toString()));
    adultSelection.addActionListener(e -> adultModeSelected(((JComboBox) e.getSource()).getSelectedItem().toString()));
    puzzleSelect.addActionListener(e -> puzzleSelected(((JComboBox) e.getSource()).getSelectedItem().toString()));
    crossoverSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / 100.;
      prefs.setCrossOverRate(value);
      crossoverInput.setText(String.valueOf(value));
    });
    genomeMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / 100.;
      prefs.setGenomeMutationRate(value);
      genomeMutationInput.setText(String.valueOf(value));
    });
    populationMutationSlider.addChangeListener(e -> {
      double value = ((AISlider) e.getSource()).getValue() / 100.;
      prefs.setPopulationMutationRate(value);
      populationMutationInput.setText(String.valueOf(value));
    });
    populationField
        .addActionListener(e -> prefs.setPopulationSize(Integer.parseInt((((AITextField) e.getSource()).getText()))));

    runButton.addActionListener(e -> run());
    stopButton.addActionListener(e -> listener.stop());

    crossoverInput.addActionListener(e -> crossoverSlider.setValue(getSliderValue(e)));
    genomeMutationInput.addActionListener(e -> genomeMutationSlider.setValue(getSliderValue(e)));
    populationMutationInput.addActionListener(e -> populationMutationSlider.setValue(getSliderValue(e)));
    bitVectorSizeInput
        .addActionListener(e -> prefs.setBitVectorSize(Integer.parseInt(((JTextField) e.getSource()).getText())));

    surprisingLengthInput
        .addActionListener(e -> prefs.setSurprisingLength(Integer.parseInt(((JTextField) e.getSource()).getText())));
    alphabetSizeInput
        .addActionListener(e -> prefs.setAlphabetSize(Integer.parseInt(((JTextField) e.getSource()).getText())));

    zeroThresholdInput
        .addActionListener(e -> prefs.setZeroThreashold(Integer.parseInt(((JTextField) e.getSource()).getText())));

    logField.setFont(Font.decode(Font.MONOSPACED));
    buildFrame(mainPanel, logField, null);

    initDefaultPreferences();
  }

  private void run() {
    prefs.setZeroThreashold(Integer.parseInt(zeroThresholdInput.getText()));
    prefs.setBitVectorSize(Integer.parseInt(bitVectorSizeInput.getText()));
    prefs.setPopulationSize(Integer.parseInt(populationField.getText()));
    prefs.setAlphabetSize(Integer.parseInt(alphabetSizeInput.getText()));
    listener.run(prefs);
  }

  private void puzzleSelected(String puzzle) {
    prefs.puzzleSelected(puzzle);
    if (puzzle.equals(SurprisingSequences.class.getSimpleName())) {
      setVisibleSurprising(true);
    } else if (puzzle.equals(Lolz.class.getSimpleName())) {
      setVisibleLolz(true);
    } else if (puzzle.equals(OneMax.class.getSimpleName())) {
      setVisibleOneMax(true);
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

  private int getSliderValue(ActionEvent e) {
    return (int) Double.parseDouble((((JTextField) e.getSource()).getText())) * 100;
  }

  private void initDefaultPreferences() {
    adultSelection.setSelectedItem(prefs.getAdultSelectionMode().getClass().getSimpleName());
    matingSelection.setSelectedItem(prefs.getMateSelectionMode().getClass().getSimpleName());
    crossoverSlider.setValue((int) (prefs.getCrossOverRate() * 100));
    crossoverInput.setText(String.valueOf(prefs.getCrossOverRate()));
    genomeMutationSlider.setValue((int) (prefs.getGenomeMutationRate() * 100));
    genomeMutationInput.setText(String.valueOf(prefs.getGenomeMutationRate()));
    populationMutationSlider.setValue((int) (prefs.getPopulationMutationRate() * 100));
    populationMutationInput.setText(String.valueOf(prefs.getPopulationMutationRate()));
    populationField.setText(String.valueOf(prefs.getPopulationSize()));
  }

  public void setListener(GeneticGuiListener listener) {
    this.listener = listener;
  }

  public void matingModeSelected(String mode) {
    if (mode.equals(FitnessProportiate.class.getSimpleName())) {
      prefs.setMateSelectionMode(new FitnessProportiate());
    } else if (mode.equals(SigmaScaled.class.getSimpleName())) {
      prefs.setMateSelectionMode(new SigmaScaled());
    } else if (mode.equals(Tournament.class.getSimpleName())) {
      prefs.setMateSelectionMode(new Tournament(getTournamentK(), getTournamentE()));
    }
  }

  private double getTournamentE() {
    return 0.05;
  }

  private int getTournamentK() {
    return 10;
  }

  public void adultModeSelected(String mode) {
    if (mode.equals(FullTurnover.class.getSimpleName())) {
      prefs.setAdultSelectionMode(new FullTurnover());
    } else if (mode.equals(Mixing.class.getSimpleName())) {
      prefs.setAdultSelectionMode(new Mixing(getMixingRate()));
    } else if (mode.equals(OverProduction.class.getSimpleName())) {
      prefs.setAdultSelectionMode(new OverProduction(getOverProductionRate()));
    }
  }

  private int getOverProductionRate() {
    return 2;
  }

  private double getMixingRate() {
    return 0.5;
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
}

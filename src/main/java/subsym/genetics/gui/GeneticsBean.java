package subsym.genetics.gui;

public class GeneticsBean {

  private double genomeMutationRate;
  private double populationMutationRate;
  private double crossoverRate;
  private int populationSize;
  private int bitVectorSize;
  private int surprisingLength;
  private int alphabetSize;
  private int zeroThreshold;
  private double mixingRate;
  private String[] tournamentPrefs;

  public GeneticsBean() {
  }

  public double getGenomeMutationRate() {
    return genomeMutationRate;
  }

  public void setGenomeMutationRate(final String genomeMutationRate) {
    this.genomeMutationRate = Double.parseDouble(genomeMutationRate);
  }

  public double getPopulationMutationRate() {
    return populationMutationRate;
  }

  public void setPopulationMutationRate(final String populationMutationRate) {
    this.populationMutationRate = Double.parseDouble(populationMutationRate);
  }

  public double getCrossoverRate() {
    return crossoverRate;
  }

  public void setCrossoverRate(final String crossoverRate) {
    this.crossoverRate = Double.parseDouble(crossoverRate);
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(final String populationSize) {
    this.populationSize = Integer.parseInt(populationSize);
  }

  public int getBitVectorSize() {
    return bitVectorSize;
  }

  public void setBitVectorSize(final String bitVectorSize) {
    this.bitVectorSize = Integer.parseInt(bitVectorSize);
  }

  public int getSurprisingLength() {
    return surprisingLength;
  }

  public void setSurprisingLength(final String surprisingLength) {
    this.surprisingLength = Integer.parseInt(surprisingLength);
  }

  public int getAlphabetSize() {
    return alphabetSize;
  }

  public void setAlphabetSize(final String alphabetSize) {
    this.alphabetSize = Integer.parseInt(alphabetSize);
  }

  public int getZeroThreshold() {
    return zeroThreshold;
  }

  public void setZeroThreshold(final String zeroThreshold) {
    this.zeroThreshold = Integer.parseInt(zeroThreshold);
  }

  public double getMixingRate() {
    return mixingRate;
  }

  public void setMixingRate(final String mixingRate) {
    this.mixingRate = Double.parseDouble(mixingRate);
  }

  public String[] getTournamentPrefs() {
    return tournamentPrefs;
  }

  public void setTournamentPrefs(final String tournamentPrefs) {
    this.tournamentPrefs = tournamentPrefs.split("/");
  }
}
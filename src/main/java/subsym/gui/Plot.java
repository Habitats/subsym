package subsym.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

import javax.swing.*;

import subsym.Log;

/**

 */
public class Plot extends JPanel {

  private static final String TAG = Plot.class.getSimpleName();
  private Component chartPanel;
  private XYSeriesCollection dataset;
  private XYSeries average;
  private XYSeries max;
  private XYSeries sd;
  private XYSeries cr;
  private XYSeries mr;
  private XYSeries pmr;

  public Plot() {
    setSingleRunDataset();
  }

  private void updateRenderer(XYLineAndShapeRenderer renderer, XYDataset dataset, String x, String y) {
    if (chartPanel != null) {
      remove(chartPanel);
    }

    JFreeChart chart = ChartFactory.createScatterPlot(getName(), x, y, dataset, //
                                                      PlotOrientation.VERTICAL, true, true, false);
    setLayout(new GridBagLayout());
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setBackground(Theme.getBackground());
    chartPanel.setForeground(Theme.getForeground());
    chartPanel.setFont(new Font("Consolas", Font.TRUETYPE_FONT, 15));
    setLayout(new BorderLayout());
    final XYPlot plot = chart.getXYPlot();
    plot.setRenderer(renderer);

    add(chartPanel, BorderLayout.CENTER);
    revalidate();
  }

  private XYLineAndShapeRenderer getLineRenderer() {
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, ColorUtils.toHsv(0.70, 1, 0.85));
    renderer.setSeriesPaint(1, ColorUtils.toHsv(0.80, 1, 0.85));
    renderer.setSeriesPaint(2, ColorUtils.toHsv(0.90, 1, 0.85));
    renderer.setSeriesStroke(0, new BasicStroke(2.0f));
    renderer.setSeriesStroke(1, new BasicStroke(2.0f));
    renderer.setSeriesStroke(2, new BasicStroke(2.0f));
    renderer.setBaseShapesFilled(true);
    return renderer;
  }

  private XYLineAndShapeRenderer getScatterRenderer() {
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, ColorUtils.toHsv(0.70, 1, 0.85));
    renderer.setSeriesPaint(1, ColorUtils.toHsv(0.80, 1, 0.85));
    renderer.setSeriesPaint(2, ColorUtils.toHsv(0.90, 1, 0.85));
    renderer.setSeriesStroke(0, new BasicStroke(2.0f));
    renderer.setSeriesStroke(1, new BasicStroke(2.0f));
    renderer.setSeriesStroke(2, new BasicStroke(2.0f));
    renderer.setBaseShapesFilled(true);
    renderer.setLinesVisible(false);
    return renderer;
  }

  public void setSingleRunDataset() {
    Log.i(TAG, "Setting single dataset ...");
    dataset = createSingleRunDataset();
    updateRenderer(getLineRenderer(), dataset, "Generations            ", "Fitness");
  }

  private XYSeriesCollection createSingleRunDataset() {
    average = new XYSeries("Average");
    max = new XYSeries("Max");
    sd = new XYSeries("Standard Deviation");
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(average);
    dataset.addSeries(max);
    dataset.addSeries(sd);
    return dataset;
  }

  public void addSingleRunValue(String series, int x, double y) {
    switch (series) {
      case "avg":
        average.add(x, y);
        break;
      case "max":
        max.add(x, y);
        break;
      case "sd":
        sd.add(x, y);
        break;
    }
  }

  public void addMultipleRunsValue(String series, double x, double y) {
    switch (series) {
      case "cr":
        cr.add(x, y);
        break;
      case "mr":
        mr.add(x, y);
        break;
      case "pmr":
        pmr.add(x, y);
        break;
    }
  }

  public void clear() {
    Log.i(TAG, "Clearing plots ...");
    dataset.getSeries().forEach(v -> ((XYSeries) v).clear());
  }

  public void setMultipleRunsDataset() {
    Log.i(TAG, "Setting multiple dataset ...");
    dataset = createMultipleRunsDataset();
    updateRenderer(getScatterRenderer(), dataset, "Generations            ", "Rate");
  }

  private XYSeriesCollection createMultipleRunsDataset() {
    cr = new XYSeries("Crossover Rate");
    mr = new XYSeries("Mutation Rate");
    pmr = new XYSeries("Population Mutation Rate");
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(cr);
    dataset.addSeries(mr);
    dataset.addSeries(pmr);
    return dataset;
  }
}

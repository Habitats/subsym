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

/**

 */
public class Plot extends JPanel {

  private XYDataset dataset;
  private XYSeries average;
  private XYSeries max;
  private XYSeries sd;

  public Plot() {
    dataset = createDataset();
    JFreeChart xylineChart = ChartFactory.createXYLineChart(getName(), "Generations            ", "Fitness", dataset, //
                                                            PlotOrientation.VERTICAL, true, true, false);

    setLayout(new GridBagLayout());
    ChartPanel chartPanel = new ChartPanel(xylineChart);
    chartPanel.setBackground(Theme.getBackground());
    chartPanel.setForeground(Theme.getForeground());
    chartPanel.setFont(new Font("Consolas", Font.TRUETYPE_FONT, 15));
    setLayout(new BorderLayout());
    final XYPlot plot = xylineChart.getXYPlot();
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, ColorUtils.toHsv(0.70, 1, 0.85));
    renderer.setSeriesPaint(1, ColorUtils.toHsv(0.80,1, 0.85));
    renderer.setSeriesPaint(2, ColorUtils.toHsv(0.90, 1, 0.85));
    renderer.setSeriesStroke(0, new BasicStroke(4.0f));
    renderer.setSeriesStroke(1, new BasicStroke(4.0f));
    renderer.setSeriesStroke(2, new BasicStroke(4.0f));
    plot.setRenderer(renderer);

    add(chartPanel, BorderLayout.CENTER);
  }

  private XYDataset createDataset() {
    average = new XYSeries("Average");
    max = new XYSeries("Max");
    sd = new XYSeries("Standard Deviation");
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(average);
    dataset.addSeries(max);
    dataset.addSeries(sd);
    return dataset;
  }

  public void addValue(String series, int x, double y) {
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

  public void clear() {
    average.clear();
    max.clear();
    sd.clear();
  }
}

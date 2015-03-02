package subsym;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

/**

 */
public class Plot extends ApplicationFrame {

  private static XYDataset dataset;
  private static XYSeries average;
  private static XYSeries max;
  private static XYSeries sd;

  private static final Plot chart;

  static {
    chart = new Plot("Genetic Stats", "Oooooooooh yeah!");
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
    chart.setVisible(true);
  }

  public Plot(String applicationTitle, String chartTitle) {
    super(applicationTitle);
    dataset = createDataset();
    JFreeChart xylineChart = ChartFactory.createXYLineChart(chartTitle, "Generations", "Fitness", dataset, //
                                                            PlotOrientation.VERTICAL, true, true, false);

    ChartPanel chartPanel = new ChartPanel(xylineChart);
    chartPanel.setPreferredSize(new Dimension(560, 367));
    final XYPlot plot = xylineChart.getXYPlot();
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.RED);
    renderer.setSeriesPaint(1, Color.GREEN);
    renderer.setSeriesPaint(2, Color.YELLOW);
    renderer.setSeriesStroke(0, new BasicStroke(4.0f));
    renderer.setSeriesStroke(1, new BasicStroke(3.0f));
    renderer.setSeriesStroke(2, new BasicStroke(2.0f));
    plot.setRenderer(renderer);
    setContentPane(chartPanel);
  }

  private XYDataset createDataset() {
    average = new XYSeries("avg");
    max = new XYSeries("max");
    sd = new XYSeries("sd");
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(average);
    dataset.addSeries(max);
    dataset.addSeries(sd);
    return dataset;
  }

  public static void addValue(String series, int x, double y) {
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

  public static void clear() {
    average.clear();
    max.clear();
    sd.clear();
  }
}

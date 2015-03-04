package subsym.genetics.gui;

import subsym.genetics.GeneticPreferences;

/**
 * Created by Patrick on 03.03.2015.
 */
public interface GeneticGuiListener {

  void run(GeneticPreferences prefs);

  void stop();
}

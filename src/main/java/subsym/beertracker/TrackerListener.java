package subsym.beertracker;

/**
 * Created by Patrick on 31.03.2015.
 */
public interface TrackerListener {

  void onCaught();

  void onAvoided();

  void onCrash();
}

/**
 *
 *  @author Loshchinin Illia S13579
 *
 */

package zad1;


public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("EUR");
    Double rate2 = s.getNBPRate();

    GUI gui = new GUI(s);
    new Thread(gui).start();
  }
}

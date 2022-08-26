import java.io.FileNotFoundException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class AlertEngine {
    EventType[] baseStats;

    // create alert engine object, using the given baseStats as alert treshold
    public AlertEngine(EventType[] baseStats) {
        this.baseStats = baseStats;
    }

    // tests alerts for days days for anomalies, prefix is the file prefix
    public void testAlerts(int days, String prefix) throws FileNotFoundException {
        AnalysisEngine analisysEngine = new AnalysisEngine(baseStats);

        // calculate alert treshold
        double sumWeights = 0;
        for (int i = 0; i < baseStats.length; i++)
            sumWeights += baseStats[i].getWeight();

        double alertLimit = 2 * sumWeights;

        System.out.println("Starting alert analisys, anomaly treshold " + alertLimit);

        boolean alert = false;

        for (int i = 0; i < days; i++) {
            /// get event totals from file for this day
            double dayTotals[] = analisysEngine.countDayEvents(prefix + i + ".txt");

            // sum all anomaly contributions
            double anomaly = 0;

            for (int j = 0; j < baseStats.length; j++) {
                // calculate anomaly contribution of this event type
                double delta = Math.abs(
                        (dayTotals[j] - baseStats[j].getMean()) * baseStats[j].getWeight() / baseStats[j].getStd());

                anomaly += delta;
            }

            if (anomaly > alertLimit) {
                System.out.println("Anomaly detectected on day " + i + " anomaly weights " + anomaly);

                alert = true;
            }
        }

        if (!alert) {
            System.out.println("No Anomaly detectected");
        }
    }
}

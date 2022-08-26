
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class AnalysisEngine {

    EventType[] eventStats;

    // analye files using the given event types
    public AnalysisEngine(EventType[] eventTypes) {
        this.eventStats = eventTypes;
    }

    // count events for day from a file
    public double[] countDayEvents(String filename) throws FileNotFoundException {
        double eventCounts[] = new double[eventStats.length];

        Scanner file = new Scanner(new FileReader(filename));
        while (file.hasNextLine()) {
            String line = file.nextLine();

            String[] parts = line.split(":");

            // skip empty lines
            if (parts.length != 3)
                continue;

            String timestamp = parts[0];/// not used
            String eventName = parts[1];
            String valueStr = parts[2];
            double value = Double.parseDouble(valueStr);
            for (int j = 0; j < eventStats.length; j++)
                if (eventStats[j].getName().equals(eventName)) {
                    eventCounts[j] += value;
                }
        }
        return eventCounts;
    }

    // process logs for multiple days,
    // returns new event stats calculated on these logs
    public EventType[] processLogs(int days, String prefix) throws FileNotFoundException {
        List<double[]> allEventCounts = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            double eventCounts[] = countDayEvents(prefix + i + ".txt");

            allEventCounts.add(eventCounts);

            /// save event totals for each day
            PrintWriter writer = new PrintWriter(new File("totals_" + prefix + i + ".txt"));
            for (int j = 0; j < eventStats.length; j++)
                writer.println(eventStats[j].getName() + ":" + eventCounts[j]);
            writer.close();

        }

        /// we will return same events, but update mean and std
        EventType[] calculatedEvents = EventType.cloneEvents(eventStats);

        {
            PrintWriter writer = new PrintWriter(new File("calculated_totals_" + prefix + ".txt"));
            PrintWriter writer1 = new PrintWriter(new File("analysis" + ".txt"));

            for (int j = 0; j < eventStats.length; j++) {
                /// sum all values to calculate mean

                double sum = 0;
                for (int i = 0; i < days; i++)
                    sum += allEventCounts.get(i)[j];

                double mean = sum / days;

                // sub squares of mean and all values to calculate std
                double sumDifferences = 0;
                for (int i = 0; i < days; i++)
                    sumDifferences += (allEventCounts.get(i)[j] - mean) * (allEventCounts.get(i)[j] - mean);

                double std = Math.sqrt(sumDifferences / days);

                /// update event std and mean from calculated data
                calculatedEvents[j].setMean(mean);
                calculatedEvents[j].setStd(std);

                writer.println(eventStats[j].getName() + ":" + mean + ":" + std);

                // print so we can see if values are close, they should not be too different
                // than the data that was used for generating the events
                System.out.println(eventStats[j].getName() + " generator stats: " + eventStats[j].getMean() + " "
                        + eventStats[j].getStd() + " calculated stats: " + mean + " " + std);

                // Continuous
                // This line of code is to get the upper quartile as mean is the middle of the
                // curve and plus one standard deviation would get the stats for upper limit
                int highest = (int) (mean + std);

                // This line of code is to get the lower quartile as mean is the middle of the
                // curve and minus one standard deviation would get the stats for lower limit:
                int smallest = (int) Math.round(mean - std);

                double result = 0;
                Random rand = new Random();
                result = smallest + ((highest - smallest) * (rand.nextDouble()));
                result = Math.round(result * 100.00) / 100.00;

                // Discrete
                int result1 = 0;
                result1 = rand.nextInt((highest + 1) - smallest) + highest;

                // Output
                System.out.println("Continuous Baseline of " + eventStats[j].getName() + " : " + result);
                System.out.println("Discrete Baseline of " + eventStats[j].getName() + " : " + result1);
                writer1.println("Continuous Baseline of " + eventStats[j].getName() + " : " + result + "\n"
                        + "Discrete Baseline of " + eventStats[j].getName() + " : " + result1);
            }
            writer.close();
            writer1.close();
        }

        return calculatedEvents;
    }

}
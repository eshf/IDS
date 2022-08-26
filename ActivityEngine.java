import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public class ActivityEngine {
    EventType[] eventStats;

    // create new activity engine object, using given stats for generating events
    public ActivityEngine(EventType[] eventStats) {
        this.eventStats = eventStats;
    }

    // time stamp for writing in file,
    String generateTimestamp() {
        int h = (int) Math.floor(Math.random() * 24);
        int m = (int) Math.floor(Math.random() * 60);
        int s = (int) Math.floor(Math.random() * 60);

        // we use - instead of : in time as : is our field separator !
        return String.format("%02d-%02d-%02d", h, m, s);
    }

    /// generate event logs for days, save files usign prefix
    void generateLogs(int days, String prefix) throws FileNotFoundException {

        for (int i = 0; i < days; i++) {
            List<String> eventLog = new ArrayList<String>();
            /// generate random values for the day for each event
            for (int j = 0; j < eventStats.length; j++) {
                // Continuous
                if (eventStats[j].getType() == 'C') {
                    /// generate continuous value for this event
                    double value = eventStats[j].getRandValue();
                    /// format to 2 digits
                    eventLog.add(
                            generateTimestamp() + ":" + eventStats[j].getName() + ":" + String.format("%.2f", value));
                } else
                // Discrete
                if (eventStats[j].getType() == 'D') {
                    /// generate number of events for this day, round to ingeter
                    int events = (int) Math.round(eventStats[j].getRandValue());

                    for (int k = 0; k < events; k++)
                        eventLog.add(generateTimestamp() + ":" + eventStats[j].getName() + ":" + 1);
                }
            }

            /// sort will produce ordering by time,
            // as the first part of the string is hh:mm:ss
            Collections.sort(eventLog);

            /*
             * try
             * {
             * FileWriter writer = new FileWriter(new File(prefix+i+".txt"));
             * for(String str : eventLog)
             * writer.write(str);
             * writer.close();
             * } catch(IOException e) {
             * System.out.println("Error");
             * e.printStackTrace();
             * }
             */

            /// save each string to file
            PrintWriter writer = new PrintWriter(new File(prefix + i + ".txt"));
            for (String str : eventLog)
                writer.println(str);
            writer.close();

        }
    }
}

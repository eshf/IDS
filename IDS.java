import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class IDS {

    // read event types from file
    static EventType[] ReadEventTypesFile(String filename) throws FileNotFoundException {
        Scanner file = new Scanner(new FileReader(filename));
        String numLine = file.nextLine();
        int events = Integer.parseInt(numLine);
        EventType[] eventTypes = new EventType[events];
        for (int i = 0; i < events; i++) {
            String line = file.nextLine();
            String[] parts = line.split(":");

            String name = parts[0];
            String type = parts[1];
            String minStr = parts[2];
            String maxStr = parts[3];
            String weightStr = parts[3];

            /// set to zero if empty
            double weightVal = 0;
            double minVal = 0;
            double maxVal = Double.MAX_VALUE;
            if (!minStr.equals(""))
                minVal = Double.parseDouble(minStr);

            /// set to max double if empty
            if (!maxStr.equals(""))
                maxVal = Double.parseDouble(maxStr);
            /// set to max double if empty
            if (!weightStr.equals(""))
                weightVal = Double.parseDouble(weightStr);

            /// save event type
            eventTypes[i] = new EventType(name, type.charAt(0), minVal, maxVal, weightVal);
        }
        return eventTypes;
    }

    // read stats file, updating each event mean and std value from the stats file
    static void ReadEventStatsFile(String filename, EventType[] eventTypes) throws FileNotFoundException {
        Scanner file = new Scanner(new FileReader(filename));
        String numLine = file.nextLine();
        int stats = Integer.parseInt(numLine);
        for (int i = 0; i < stats; i++) {

            String line = file.nextLine();
            String[] parts = line.split(":");

            String name = parts[0];
            String meanStr = parts[1];
            String stdStr = parts[2];

            double meanVal = Double.parseDouble(meanStr);
            double stdVal = Double.parseDouble(stdStr);

            /// update mean and std value of the matching event type
            for (int j = 0; j < eventTypes.length; j++)
                if (eventTypes[j].getName().equals(name)) {
                    eventTypes[j].setMean(meanVal);
                    eventTypes[j].setStd(stdVal);
                }

        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        EventType[] initialStats;

        if (args.length != 3) {
            System.out.println("Usage: IDS Events.txt Stats.txt Days");
            System.exit(-1);
        }

        initialStats = ReadEventTypesFile(args[0]);

        ReadEventStatsFile(args[1], initialStats);

        int baseDays = Integer.parseInt(args[2]);

        // Activity Engine - each base of day
        ActivityEngine baseActivityEngine = new ActivityEngine(initialStats);
        System.out.println("Generating Events");
        baseActivityEngine.generateLogs(baseDays, "base_day");
        System.out.println("Done");

        // Analysis Engine - total of calculated
        AnalysisEngine baseAnalysysEngine = new AnalysisEngine(initialStats);
        System.out.println("Analyzing Events");
        EventType[] baseStats = baseAnalysysEngine.processLogs(baseDays, "base_day");
        System.out.println("Done");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Enter StatsFile Days: ");
            String line = sc.nextLine();
            if (line.equals("quit"))
                break;

            System.out.println(line);
            String parts[] = line.split(" ");

            if (parts.length == 2)
            // if(line.equals("TestStats1.txt 10"))
            {

                int newDays = Integer.parseInt(parts[1]);
                EventType[] newStats = EventType.cloneEvents(initialStats);
                ReadEventStatsFile(parts[0], newStats);

                ActivityEngine liveActivityEngine = new ActivityEngine(newStats);
                System.out.println("Generating Events1");
                liveActivityEngine.generateLogs(newDays, "live_day");
                System.out.println("Done");

                AlertEngine alertEngine = new AlertEngine(baseStats);

                alertEngine.testAlerts(newDays, "live_day");

            } else {
                System.out.println("Expected: StatsFile Days");
            }
        }
    }

}

import java.util.Random;

class EventType {
    String name;
    char type;
    double min;
    double max;
    double weight;

    double mean;
    double std;

    public static EventType[] cloneEvents(EventType[] arg) {
        EventType newEvents[] = new EventType[arg.length];

        for (int i = 0; i < arg.length; i++)
            newEvents[i] = new EventType(arg[i]);

        return newEvents;
    }

    public EventType(EventType copy) {
        this.name = copy.name;
        this.type = copy.type;
        this.min = copy.min;
        this.max = copy.max;
        this.weight = copy.weight;

        this.mean = copy.mean;
        this.std = copy.std;
    }

    public EventType(String name, char type, double min, double max, double weight) {
        this.name = name;
        this.type = type;
        this.min = min;
        this.max = max;
        this.weight = weight;

        /// set some default values in case we dont update this
        this.mean = (min + max) / 2;
        this.std = (min - max) / 2;
    }

    public String getName() {
        return name;
    }

    public char getType() {
        return type;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getStd() {
        return std;
    }

    public double getWeight() {
        return weight;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getRandValue() {

        Random rand = new Random();
        /// next gausian has mean 0 and std dev 1, so we scale and add
        while (true) {
            double value = mean + std * rand.nextGaussian();

            if (type == 'D')/// if descreete round to whole
                value = Math.round(value);

            if (value >= min && value <= max) {/// if accepted by min/max limits
                return value;
            }
        }
    }

};
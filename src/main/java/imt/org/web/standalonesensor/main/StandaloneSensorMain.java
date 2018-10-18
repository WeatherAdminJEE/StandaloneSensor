package imt.org.web.standalonesensor.main;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.standalonesensor.publisher.Publisher;
import imt.org.web.standalonesensor.publisher.http.HTTPPublisher;
import imt.org.web.standalonesensor.publisher.mqtt.MQTTPublisher;

import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class
 */
public class StandaloneSensorMain {

    // Config file
    public static final ResourceBundle CONFIG = ResourceBundle.getBundle("config");
    private static final long timeInterval = Long.parseLong(CONFIG.getString("timeInterval"));

    /**
     * Main
     * @param args Main args
     */
    public static void main(String[] args) {

        System.out.println("StandaloneSensor!");

        String mode = "";
        int idSensor = 0;
        String country = "";
        String city = "";
        String gpsCoordinates = "";
        MeasureType measureType = null;

        // Parse args
        for (int i=0; i<args.length; i++) {
            if (args[i].length() == 2 && args[i].startsWith("-")) {
                char arg = args[i].charAt(1);
                switch (arg) {
                    case 'h':
                        printHelp();
                        return;
                }
                if (i == args.length - 1 || args[i + 1].charAt(0) == '-') {
                    System.out.println("Missing value for argument: " + args[i]);
                    printHelp();
                    return;
                }
                switch (arg) {
                    case 'm':
                        mode = args[++i];
                        break;
                    case 'i':
                        idSensor = Integer.parseInt(args[++i]);
                        break;
                    case 'p':
                        country = args[++i];
                        break;
                    case 'v':
                        city = args[++i];
                        break;
                    case 'g':
                        gpsCoordinates = args[++i];
                        break;
                    case 't':
                        measureType = setMeasureType(args[++i]);
                        break;
                    default:
                        System.out.println("Unrecognised argument: " + args[i]);
                        printHelp();
                        return;
                }
            } else {
                System.out.println("Unrecognised argument: " + args[i]);
                printHelp();
                return;
            }
        }

        // Send data loops
        Publisher publisher = null;
        if("http".equals(mode)) {
            publisher = new HTTPPublisher(idSensor, country, city, gpsCoordinates, measureType);
        } else if ("mqtt".equals(mode)) {
            publisher = new MQTTPublisher(idSensor, country, city, gpsCoordinates, measureType);
        } else {
            System.out.println("Invalid value for argument: -m");
            printHelp();
            System.exit(0);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(publisher, 0, timeInterval, TimeUnit.SECONDS);
    }

    /**
     * Set MeasureType from main args
     * @param measureTypeArg Main measure type arg
     * @return MeasureType
     */
    private static MeasureType setMeasureType(String measureTypeArg) {
        switch(measureTypeArg) {
            case "temp":
                return MeasureType.TEMPERATURE;
            case "pres":
                return MeasureType.ATM_PRESSURE;
            case "wspeed":
                return MeasureType.WIND_SPEED;
            case "wdir":
                return MeasureType.WIND_DIRECTION;
            default:
                System.out.println("Invalid value for argument: -t");
                printHelp();
                System.exit(0);
                return null;
        }
    }

    /**
     * Print help
     */
    private static void printHelp() {
        System.out.println(
            "Help:\n\n" +
                    "    Sample [-h] [-m <mode>] [-i <ID sensor>] [-p <country>] [-v <city>] [-g <GPS coordinates>] [-t <measure type>]\n\n" +
                    "    -h  Print this help text and quit\n" +
                    "    -m  Desired mode (mqtt, http)\n" +
                    "    -i  Sensor ID\n" +
                    "    -p  Desired country\n" +
                    "    -v  Desired city\n" +
                    "    -g  GPS coordinates\n" +
                    "    -t  Measure type (temp, pres, wspeed, wdir)\n"
        );
    }
}

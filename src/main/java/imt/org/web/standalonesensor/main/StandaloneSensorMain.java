package imt.org.web.standalonesensor.main;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.standalonesensor.publisher.IPublisher;
import imt.org.web.standalonesensor.publisher.http.HTTPPublisher;
import imt.org.web.standalonesensor.publisher.mqtt.MQTTPublisher;

import org.apache.commons.math3.util.Precision;

import java.util.Date;
import java.sql.Timestamp;
import java.util.ResourceBundle;

/**
 * Main class
 */
public class StandaloneSensorMain {

    // Config file
    public static final ResourceBundle CONFIG = ResourceBundle.getBundle("config");

    /**
     * Main
     * @param args Main args
     */
    public static void main(String[] args) {

        System.out.println("StandaloneSensor!");

        String clientId = "";
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
                        clientId = "Sensor" + idSensor;
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
        try {
            IPublisher publisher = null;
            if("http".equals(mode)) {
                publisher = new HTTPPublisher();
            } else if ("mqtt".equals(mode)) {
                publisher = new MQTTPublisher(clientId);
            }
            while(true) {
                SensorData temp = generateSensorData(idSensor, country, city, gpsCoordinates, measureType);
                publisher.publish(temp);
                Thread.sleep(10000);
            }
        } catch (InterruptedException ex) {
            System.out.println("Send data loop error : " + ex.getMessage());
        }
    }

    /**
     * Set MeasureType from main args
     * @param measureTypeArg Main measure type arg
     * @return MeasureType
     */
    public static MeasureType setMeasureType(String measureTypeArg) {
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
                return null;
        }
    }

    /**
     * Generate random data
     * @param idSensor ID Sensor
     * @param country ID Country
     * @param city ID city
     * @param gpsCoordinates GPS coordinates
     * @param measureType Measure type
     * @return Random data
     */
    static SensorData generateSensorData(int idSensor, String country, String city, String gpsCoordinates, MeasureType measureType) {
        double measureValue = 0.0;
        switch(measureType) {
            case TEMPERATURE:
                measureValue = Precision.round(17 + Math.random() * 11,2);
                break;
            case ATM_PRESSURE:
                measureValue = Precision.round(1010 + Math.random() * 5,2);
                break;
            case WIND_SPEED:
                measureValue = Precision.round(Math.random() * 60,2);
                break;
            case WIND_DIRECTION:
                measureValue = Precision.round(Math.random() * 60,2);
                break;
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return new SensorData(idSensor, country, city, gpsCoordinates, measureType, measureValue, timestamp);
    }

    /**
     * Print help
     */
    static void printHelp() {
        System.out.println(
            "Help:\n\n" +
                    "    Sample [-h] [-m <mode>] [-i <id Sensor>] [-p <country>] [-v <city>]\n\n" +
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

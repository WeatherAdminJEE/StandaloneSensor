package imt.org.web.standalonesensor.main;

import imt.org.web.commonmodel.Measure;
import imt.org.web.commonmodel.SensorData;
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

        IPublisher publisher;
        String clientId = "";
        String mode = "";
        int idSensor = 0;
        String country = "";
        String city = "";

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
            if("http".equals(mode)) {
                publisher = new HTTPPublisher();
                while(true) {
                    SensorData temp = generateSensorData(idSensor, country, city);
                    publisher.publish(temp);
                    Thread.sleep(10000);
                }
            } else if ("mqtt".equals(mode)) {
                publisher = new MQTTPublisher(clientId);
                while(true) {
                    SensorData temp = generateSensorData(idSensor, country, city);
                    publisher.publish(temp);
                    Thread.sleep(10000);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Send data loop error : " + ex.getMessage());
        }
    }

    /**
     * Generate random data
     * @param idSensor Sensor ID
     * @param country Country
     * @param city City
     * @return
     */
    static SensorData generateSensorData(int idSensor, String country, String city) {
        Measure measure = new Measure(
            Precision.round(17 + Math.random() * 11,2),
            Precision.round(Math.random() * 60,2),
            Precision.round(1010 + Math.random() * 5,2)
        );
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return new SensorData(idSensor, country, city, measure, timestamp);
    }

    /**
     * Print help
     */
    static void printHelp() {
        System.out.println(
            "Help:\n\n" +
                    "    Sample [-h] [-m <mode>] [-i <id Sensor>] [-p <country>] [-v <city>]\n\n" +
                    "    -h  Print this help text and quit\n" +
                    "    -m  Desired mode (mqtt or http)\n" +
                    "    -i  Sensor ID\n" +
                    "    -p  Desired country\n" +
                    "    -v  Desired city\n"
        );
    }
}

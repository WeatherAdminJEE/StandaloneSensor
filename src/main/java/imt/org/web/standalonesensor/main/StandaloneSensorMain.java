package imt.org.web.standalonesensor.main;

import imt.org.web.commonmodel.Measure;
import imt.org.web.commonmodel.SensorData;
import imt.org.web.standalonesensor.Publisher;
import imt.org.web.standalonesensor.http.HTTPPublisher;
import imt.org.web.standalonesensor.mqtt.MQTTPublisher;
import org.apache.commons.math3.util.Precision;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Date;
import java.sql.Timestamp;

public class StandaloneSensorMain {

    public static void main(String[] args) {

        // Test module dependency

        System.out.println("StandaloneSensor!");
        //SensorData test = new SensorData();

        Publisher publisher;

        String topic = "foo";
        int qos = 2;
        String clientId = "Jean Cule";
        String broker = "barnab2.tk";
        int port = 21883;
        boolean cleanSession = true; // Non durable subscriptions
        String protocol = "tcp://";
        String url = protocol + broker + ":" + port;

        String mode = "";
        int idSensor = 0;
        String country = "";
        String city = "";

        // Parse the arguments
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

        try {
            boolean loop = true;
            if("http".equals(mode)) {
                publisher = new HTTPPublisher();
                while(loop) {
                    SensorData temp = generateSensorData(idSensor, country, city);
                    ((HTTPPublisher) publisher).postPublish(temp);
                    Thread.sleep(10000);
                    if(idSensor == 0) {
                        loop = false;
                    }
                }
            } else if ("mqtt".equals(mode)) {
                publisher = new MQTTPublisher(url, clientId, cleanSession);
                while(loop) {
                    SensorData temp = generateSensorData(idSensor, country, city);
                    ((MQTTPublisher) publisher).mqttPublish(topic, qos, temp);
                    Thread.sleep(10000);
                    if(idSensor == 0) {
                        loop = false;
                    }
                }
            }
        } catch(MqttException me) {
            System.out.println("MQTT Error : " + me.getMessage());
        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    static SensorData generateSensorData(int idSensor, String country, String city) {
        Measure measure = new Measure(
                Precision.round(Math.random()*30,2),
                Precision.round(Math.random()*90,2),
                Precision.round(1000 + Math.random()*10,2)
        );
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return new SensorData(idSensor, country, city, measure, timestamp);
    }

    static void printHelp() {
        System.out.println(
            "Syntax:\n\n" +
                    "    Sample [-h] [-m <mode>] [-i <id Sensor>] [-p <country>] [-v <city>]\n\n" +
                    "    -h  Print this help text and quit\n" +
                    "    -m  Desired mode (MQTT or HTTP)\n" +
                    "    -i  Sensor ID\n" +
                    "    -p  Desired country\n" +
                    "    -v  Desired city\n"
        );
    }
}

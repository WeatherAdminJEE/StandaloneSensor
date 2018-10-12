package imt.org.web.standalonesensor.main;

import imt.org.web.commonmodel.SensorData;
import imt.org.web.standalonesensor.mqtt.MQTTPublisher;
import org.eclipse.paho.client.mqttv3.MqttException;

public class StandaloneSensorMain {

    public static void main(String[] args) {

        // Test module dependency
        SensorData test = new SensorData(20);

        System.out.println("StandaloneSensor!");

        String topic = "test";
        int qos = 2;
        String broker = "localhost";
        int port = 1883;
        String clientId = "Jean";
        boolean cleanSession = true; // Non durable subscriptions

        String protocol = "tcp://";
        String url = protocol + broker + ":" + port;

        try {
            // Create an instance of this class
            MQTTPublisher publisher = new MQTTPublisher(url, clientId, cleanSession);

            publisher.publish(topic, qos, "Wesh les kheys !".getBytes());
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("Error msg " +me.getMessage());
        }
    }
}

package imt.org.web.standalonesensor.mqtt;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

@Getter
@Setter
public class MQTTPublisher implements MqttCallback {

    private MqttClient client;
    private String brokerUrl;
    private boolean cleanSession;

    public MQTTPublisher(String brokerUrl, String clientId, boolean cleanSession) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.cleanSession = cleanSession;

        //This sample stores in a temporary directory... where messages temporarily
        // stored until the message has been delivered to the server.
        //..a real application ought to store them somewhere
        // where they are not likely to get deleted or tampered with
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct an MQTT blocking mode client
            client = new MqttClient(this.brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);
        } catch (MqttException e) {
            System.out.println("Error msg " + e.getMessage());
            System.out.println("Unable to set up client: " + e.toString());
            System.exit(1);
        }
    }

    /**
     * Publish a message to an MQTT server
     * @param topicName name of the topic to publish to
     * @param qos quality of service to delivery the message at (0,1,2)
     * @param payload bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

        // Connect to the MQTT server
        System.out.println("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
        client.connect();
        System.out.println("Connected");

        System.out.println("Publishing to topic \"" + topicName+"\" qos " + qos);

        // Create and configure a message
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Send the message to the server
        client.publish(topicName, message);

        // Disconnect the client
        client.disconnect();
        System.out.println("Disconnected");
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        System.out.println("Connection to " + brokerUrl + " lost : " + cause);
        System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a message has been delivered to the server
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) {
        // Called when a message arrives from the server that matches any subscription made by the client
    }
}

package imt.org.web.standalonesensor.publisher.mqtt;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.standalonesensor.generator.SensorDataGenerator;
import imt.org.web.standalonesensor.publisher.Publisher;
import imt.org.web.standalonesensor.main.StandaloneSensorMain;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * MQTT Publisher class
 */
public class MQTTPublisher extends Publisher implements MqttCallback {

    // MQTT broker params
    private MqttClient client;
    private MqttConnectOptions connectOptions;
    private String brokerUrl;
    private String topic;
    private int qos;
    private boolean cleanSession;

    /**
     * Constructor
     * @param idSensor ID Sensor
     * @param idCountry ID Country
     * @param idCity ID City
     * @param gpsCoordinates GPS coordinates
     */
    public MQTTPublisher(int idSensor, String idCountry, String idCity, String gpsCoordinates, MeasureType measureType) {
        initMQTTPublisher();

        // Init sensor params
        setIdSensor(idSensor);
        setIdCountry(idCountry);
        setIdCity(idCity);
        setGpsCoordinates(gpsCoordinates);
        setMeasureType(measureType);

        // Temp directory
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(cleanSession);

            // Construct an MQTT blocking mode client
            client = new MqttClient(this.brokerUrl, "Sensor"+idSensor, dataStore);
            client.setCallback(this);
        } catch (MqttException e) {
            System.out.println("MQTTPublisher() - Unable to set up client : " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Init MQTTPublisher properties
     */
    private void initMQTTPublisher() {
        String url = StandaloneSensorMain.CONFIG.getString("MQTTBroker");
        String port = StandaloneSensorMain.CONFIG.getString("MQTTPort");
        String protocol = "tcp://";
        brokerUrl = protocol + url + ":" + port;
        cleanSession = true;
        qos = 2;
        topic = StandaloneSensorMain.CONFIG.getString("MQTTTopic");
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        publish(SensorDataGenerator.generate(getIdSensor(), getIdCountry(), getIdCity(), getGpsCoordinates(), getMeasureType()));
    }

    /**
     * Publish a message to an MQTT server
     * @param sensorData Sensor data to send to the MQTT server
     */
    @Override
    public void publish(SensorData sensorData) {
        try {
            // Connect client
            System.out.println("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
            client.connect(connectOptions);
            System.out.println("Connected");

            printSentData(sensorData);

            // Create MQTT message
            MqttMessage message = new MqttMessage(serializeSensorData(sensorData));
            message.setQos(qos);

            // Publish message
            System.out.println("MQTT - Publishing to " + brokerUrl + " - topic " + topic);
            client.publish(topic, message);

            // Disconnect client
            client.disconnect();
            System.out.println("Disconnected");
        } catch(MqttException me) {
            System.out.println("publish() - Unable to publish message : " + me.getMessage());
        } catch (IOException ex) {
            System.out.println("publish() - Unable to serialize object : " + ex.getMessage());
        }
    }

    /**
     * Serialize a SensorData object
     * @param sensorData SensorData to serialize
     * @return Serialized SensorData
     * @throws IOException
     */
    public byte[] serializeSensorData(SensorData sensorData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput;
        objectOutput = new ObjectOutputStream(outputStream);
        objectOutput.writeObject(sensorData);
        objectOutput.flush();
        return outputStream.toByteArray();
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        System.out.println("connectionLost() - Connection to " + brokerUrl + " lost : " + cause);
        try {
            client.connect(connectOptions);
        } catch (MqttException e) {
            System.out.println("connectionLost() - Unable to reconnect broker - " + e.getMessage());
        }
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a message has been delivered to the server
        System.out.println("deliveryComplete() - Message has been successfully delivered");
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // Unused - Called when a message arrives from the server
    }
}

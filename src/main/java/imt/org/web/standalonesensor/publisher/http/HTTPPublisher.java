package imt.org.web.standalonesensor.publisher.http;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.standalonesensor.generator.SensorDataGenerator;
import imt.org.web.standalonesensor.publisher.Publisher;
import imt.org.web.standalonesensor.main.StandaloneSensorMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTP Publisher class
 */
public class HTTPPublisher extends Publisher {

    // SensorDataReceiver param
    private String serverUrl;

    /**
     * Constructor
     * @param idSensor ID Sensor
     * @param idCountry ID Country
     * @param idCity ID City
     * @param gpsCoordinates GPS coordinates
     */
    public HTTPPublisher(int idSensor, String idCountry, String idCity, String gpsCoordinates, MeasureType measureType) {
        serverUrl = StandaloneSensorMain.CONFIG.getString("SensorServer");

        // Init sensor params
        setIdSensor(idSensor);
        setIdCountry(idCountry);
        setIdCity(idCity);
        setGpsCoordinates(gpsCoordinates);
        setMeasureType(measureType);
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        publish(SensorDataGenerator.generate(getIdSensor(), getIdCountry(), getIdCity(), getGpsCoordinates(), getMeasureType()));
    }

    /**
     * Publish a message to an HTTP server
     * @param sensorData Sensor data to send to the HTTP server
     */
    @Override
    public void publish(SensorData sensorData) {
        try {
            // Open connection
            URL url = new URL(serverUrl);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

            printSentData(sensorData);

            // Add POST request params
            writer.write(setPOSTParams(sensorData));
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("HTTP - Publishing to " + serverUrl);
            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println("publish() - Error sending POST request : " + ex.getMessage());
        }
    }

    /**
     * Set POST request params
     * @param sensorData SensorData to set params
     * @return URI POST params
     */
    public String setPOSTParams(SensorData sensorData) {
        return "idSensor="+String.valueOf(sensorData.getIdSensor())
                +"&idCountry="+sensorData.getIdCountry()
                +"&idCity="+sensorData.getIdCity()
                +"&gpsCoordinates="+sensorData.getGpsCoordinates()
                +"&measureType="+sensorData.getMeasureType()
                +"&measureValue="+sensorData.getMeasureValue()
                +"&timestamp="+sensorData.getDate().toString();
    }
}

package imt.org.web.standalonesensor.publisher.http;

import imt.org.web.commonmodel.SensorData;
import imt.org.web.standalonesensor.publisher.IPublisher;
import imt.org.web.standalonesensor.main.StandaloneSensorMain;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTP Publisher class
 */
@Getter
@Setter
public class HTTPPublisher implements IPublisher {

    private final String USER_AGENT = "Mozilla/5.0";
    private String serverUrl;

    /**
     * Constructor
     */
    public HTTPPublisher() {
        serverUrl = StandaloneSensorMain.CONFIG.getString("SensorServer");
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

            System.out.println(
                "idSensor:"+String.valueOf(sensorData.getIdSensor())+"\n"
                +"idCountry"+sensorData.getIdCountry()+"\n"
                +"idCity:"+sensorData.getIdCity()+"\n"
                +"temperature:"+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
                +"windSpeed:"+String.valueOf(sensorData.getMeasure().getWindSpeed())+"\n"
                +"pressure:"+String.valueOf(sensorData.getMeasure().getPressure())+"\n"
                +"timestamp:"+sensorData.getDate().toString()
            );

            // Add POST request params
            writer.write("idSensor="+String.valueOf(sensorData.getIdSensor())
                +"&idCountry="+sensorData.getIdCountry()
                +"&idCity="+sensorData.getIdCity()
                +"&temperature="+String.valueOf(sensorData.getMeasure().getTemperature())
                +"&windSpeed="+String.valueOf(sensorData.getMeasure().getTemperature())
                +"&pressure="+String.valueOf(sensorData.getMeasure().getTemperature())
                +"&timestamp="+sensorData.getDate().toString());
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
}

package imt.org.web.standalonesensor.http;

import imt.org.web.commonmodel.SensorData;
import imt.org.web.standalonesensor.Publisher;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

@Getter
@Setter
public class HTTPPublisher extends Publisher  {

    private final String USER_AGENT = "Mozilla/5.0";

    public HTTPPublisher() {

    }

    public void postPublish(SensorData sensorData) throws Exception {

        URL url = new URL("http://localhost:8080/SensorDataReceiver/sensorData");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

        writer.write("idSensor="+String.valueOf(sensorData.getIdSensor())+"\n"
                        +"&idCountry="+sensorData.getIdCountry()+"\n"
                        +"&idCity="+sensorData.getIdCity()+"\n"
                        +"&temperature="+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
                        +"&windSpeed="+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
                        +"&pressure="+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
                        +"&timestamp="+sensorData.getDate().toString());
        System.out.println(
                "idSensor:"+String.valueOf(sensorData.getIdSensor())+"\n"
                        +"idCountry"+sensorData.getIdCountry()+"\n"
                        +"idCity:"+sensorData.getIdCity()+"\n"
                        +"temperature:"+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
                        +"windSpeed:"+String.valueOf(sensorData.getMeasure().getWindSpeed())+"\n"
                        +"pressure:"+String.valueOf(sensorData.getMeasure().getPressure())+"\n"
                        +"timestamp:"+sensorData.getDate().toString()
        );

        writer.flush();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        writer.close();
        reader.close();
    }
}

package imt.org.web.standalonesensor.publisher;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.commonmodel.model.SensorData;

import lombok.Getter;
import lombok.Setter;

/**
 * Publisher abstract class
 */
@Setter
@Getter
public abstract class Publisher implements Runnable {

    // Sensor params
    private int idSensor;
    private String idCountry;
    private String idCity;
    private String gpsCoordinates;
    private MeasureType measureType;

    /**
     * Publish a message
     * @param sensorData Sensor data to send
     */
    public abstract void publish(SensorData sensorData);

    /**
     * System.out sent SensorData
     * @param sensorData SensorData to print
     */
    public void printSentData(SensorData sensorData) {
        System.out.println(
            "idSensor:"+String.valueOf(sensorData.getIdSensor())+"\n"
            +"idCountry:"+sensorData.getIdCountry()+"\n"
            +"idCity:"+sensorData.getIdCity()+"\n"
            +"gpsCoordinates:"+sensorData.getGpsCoordinates()+"\n"
            +"measureType:"+sensorData.getMeasureType()+"\n"
            +"measureValue:"+sensorData.getMeasureValue()+"\n"
            +"timestamp:"+sensorData.getDate().toString()
        );
    }
}

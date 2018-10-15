package imt.org.web.standalonesensor.publisher;

import imt.org.web.commonmodel.model.SensorData;

/**
 * Publisher interface
 */
public interface IPublisher {

    /**
     * Publish a message
     * @param sensorData Sensor data to send
     */
    void publish(SensorData sensorData);

    /**
     * System.out sent SensorData
     * @param sensorData SensorData to print
     */
    default void printSentData(SensorData sensorData) {
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

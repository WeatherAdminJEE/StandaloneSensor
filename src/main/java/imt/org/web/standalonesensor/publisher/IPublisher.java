package imt.org.web.standalonesensor.publisher;

import imt.org.web.commonmodel.SensorData;

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
            +"temperature:"+String.valueOf(sensorData.getMeasure().getTemperature())+"\n"
            +"windSpeed:"+String.valueOf(sensorData.getMeasure().getWindSpeed())+"\n"
            +"pressure:"+String.valueOf(sensorData.getMeasure().getPressure())+"\n"
            +"timestamp:"+sensorData.getDate().toString()
        );
    }
}

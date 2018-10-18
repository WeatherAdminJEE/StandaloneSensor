package imt.org.web.standalonesensor.generator;

import imt.org.web.commonmodel.model.MeasureType;
import imt.org.web.commonmodel.model.SensorData;
import org.apache.commons.math3.util.Precision;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Random SensorData generator class
 */
public class SensorDataGenerator {

    /**
     * Generate random data
     * @param idSensor ID Sensor
     * @param country ID Country
     * @param city ID City
     * @param gpsCoordinates GPS coordinates
     * @param measureType Measure type
     * @return Random data
     */
    public static SensorData generate(int idSensor, String country, String city, String gpsCoordinates, MeasureType measureType) {
        double measureValue = 0.0;
        switch(measureType) {
            case TEMPERATURE:
                measureValue = Precision.round(17 + Math.random() * 11,2);
                break;
            case ATM_PRESSURE:
                measureValue = Precision.round(1010 + Math.random() * 5,2);
                break;
            case WIND_SPEED:
                measureValue = Precision.round(Math.random() * 60,2);
                break;
            case WIND_DIRECTION:
                measureValue = Precision.round(Math.random() * 60,2);
                break;
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return new SensorData(idSensor, country, city, gpsCoordinates, measureType, measureValue, timestamp);
    }
}

package imt.org.web.standalonesensor.publisher;

import imt.org.web.commonmodel.SensorData;

/**
 * Publisher interface
 */
public interface IPublisher {

    void publish(SensorData sensorData);
}

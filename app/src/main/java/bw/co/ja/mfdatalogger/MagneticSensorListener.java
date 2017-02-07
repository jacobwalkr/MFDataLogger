package bw.co.ja.mfdatalogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Jacob on 26/01/2017.
 */

public class MagneticSensorListener implements SensorEventListener {

    private final DataWriter writer;
    private boolean errorOccurred = false;
    private String errorMessage = null;

    public MagneticSensorListener(DataWriter writer) {
        this.writer = writer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            String data = String.format(Locale.UK, "%f %f %f", event.values[0], event.values[1],
                    event.values[2]);
            try {
                this.writer.writeData(data);
            } catch (IOException e) {
                e.printStackTrace();
                this.errorOccurred = true;
                this.errorMessage = e.getMessage();
            }
        }
    }

    boolean errorOccurred() {
        return this.errorOccurred;
    }

    String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing :o
    }
}

package bw.co.ja.mfdatalogger;

import android.hardware.SensorManager;
import android.widget.EditText;

/**
 * Created by Jacob on 21/01/2017.
 */

class Utils {

    public static String viewToString(EditText view) {
        return view.getText().toString().trim();
    }

    public static int viewToInt(EditText view) {
        return Integer.parseInt(viewToString(view));
    }

    public static boolean textIsEmpty(EditText view) {
        if (viewToString(view).equals("")) {
            return true;
        }

        return false;
    }

    public static int parseFrequencyLabel(String frequencyString) {
        switch (frequencyString) {
            case "FASTEST":
                return SensorManager.SENSOR_DELAY_FASTEST;
            case "NORMAL":
                return SensorManager.SENSOR_DELAY_NORMAL;
            case "UI":
                return SensorManager.SENSOR_DELAY_UI;
            default:
                return SensorManager.SENSOR_DELAY_GAME;
        }
    }
}

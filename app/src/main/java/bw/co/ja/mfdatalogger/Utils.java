package bw.co.ja.mfdatalogger;

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
}

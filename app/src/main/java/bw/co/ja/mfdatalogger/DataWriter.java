package bw.co.ja.mfdatalogger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jacob on 26/01/2017.
 */

class DataWriter {

    private final Context context;
    private final CaptureDetails captureDetails;
    private final BufferedWriter writer;
    private File outFile;

    DataWriter(Context context, CaptureDetails captureDetails) throws IOException {
        this.context = context;
        this.captureDetails = captureDetails;
        this.writer = getWriter();

        // Write preamble
        this.write("# " + captureDetails.getFullRoomName());
        this.write("# " + captureDetails.locationDetail);
        this.write("# " + captureDetails.localActivity);
        this.write("# " + String.format(Locale.UK, "%d %s", captureDetails.duration,
                this.context.getResources().getString(R.string.label_new_capture_time_unit)));
        this.write("# " + captureDetails.sampleFrequencyString);
    }

    public void writeData(String data) throws IOException {
        DateTime now = new DateTime();
        this.write(String.format("%s %s", now.toStringForWrite(), data));
    }

    private void write(String line) throws IOException {
        try {
            this.writer.write(line + "\n");
        } catch (IOException e) {
            Log.e("File", String.format("error when writing: %s", this.outFile.toString()));
            e.printStackTrace();
            throw e;
        }
    }

    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_LONG);
            Log.e("File", String.format("unable to close file: %s", this.outFile.toString()));
            e.printStackTrace();
        }
    }

    private BufferedWriter getWriter() throws IOException {
        // Get a handle for the file we'll be writing readings to
        DateTime now = new DateTime();
        this.outFile = new File(getOutputFolder(),
                String.format("%s_%s.mflog", this.captureDetails.getFullRoomName(),
                        now.toStringForFilename()));

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(outFile, true));

            Log.d("File", String.format("opened output file: %s", this.outFile.toString()));
            Toast.makeText(this.context, String.format(
                    this.context.getResources().getString(R.string.start_writing),
                    this.outFile.getName()), Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.context, R.string.create_file_error, Toast.LENGTH_LONG).show();
            throw e;
        }

        return writer;
    }

    private File getOutputFolder() {
        File file;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "MFDataLogger");

            Log.d("in getOutputFolder", file.toString());

            if (file.exists() || file.mkdirs()) {
                file.setWritable(true);
                Log.d("File", String.format("got output folder: %s", file.toString()));
                return file;
            }
        }

        // Not returned? Error!
        Toast.makeText(this.context, R.string.create_file_error, Toast.LENGTH_LONG).show();
        return null;
    }

    private class DateTime {

        private final int year;
        private final int month;
        private final int day;
        private final int hour;
        private final int minute;
        private final int second;
        private final int millis;

        DateTime() {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1; // because months are uniquely zero-indexed
            day = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            second = cal.get(Calendar.SECOND);
            millis = cal.get(Calendar.MILLISECOND);
        }

        public String toStringForFilename() {
            return String.format(Locale.UK, "%1$d-%2$02d-%3$02d_%4$02d-%5$02d-%6$02d", year, month,
                    day, hour, minute, second);
        }

        public String toStringForWrite() {
            return String.format(Locale.UK, "%1$d-%2$02d-%3$02dT%4$02d:%5$02d:%6$02d.%7$d", year,
                    month, day, hour, minute, second, millis);
        }
    }
}

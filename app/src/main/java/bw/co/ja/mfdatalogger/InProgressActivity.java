package bw.co.ja.mfdatalogger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class InProgressActivity extends AppCompatActivity {

    private CaptureDetails captureDetails;
    private CountDownTimer captureTimer;
    private DataWriter writer;
    private SensorManager sensorManager;
    private MagneticSensorListener magneticSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Want a black title bar and can't be arsed to faff
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(colorDrawable);
        bar.setTitle("");

        // Get saved details from Intent
        Intent intent = getIntent();
        this.captureDetails = intent.getParcelableExtra("details");

        TextView roomLabel = (TextView) findViewById(R.id.text_in_progress_room);
        roomLabel.setText(captureDetails.getFullRoomName());

        // Set up listener
        try {
            this.writer = new DataWriter(getApplicationContext(), captureDetails);
        } catch (IOException e) {
            finishCaptureAfterError(e.getMessage());
        }

        magneticSensorListener = new MagneticSensorListener(this.writer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(magneticSensorListener, magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);

        startTimer();
    }

    protected void startTimer() {
        final TextView timerView = (TextView) findViewById(R.id.text_in_progress_countdown);

        captureTimer = new CountDownTimer(this.captureDetails.duration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerView.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                InProgressActivity.this.finishSuccessfulCapture();
            }
        }.start();
    }

    protected String formatTime(long millis) {
        int seconds = (int) (millis / 1000);
        int remainderSeconds = seconds % 60;
        int wholeMinutes = seconds / 60;

        return String.format(Locale.UK, "%02d:%02d", wholeMinutes, remainderSeconds);
    }

    public void cancelCapture(View view) {
        this.captureTimer.cancel();
        sendResult(RESULT_CANCELED, getResources().getString(R.string.snackbar_new_capture_canceled));
    }

    protected void finishSuccessfulCapture() {
        if (this.magneticSensorListener.errorOccurred()) {
            finishCaptureAfterError(magneticSensorListener.getErrorMessage());
        }
        else {
            sendResult(RESULT_OK, getResources().getString(R.string.snackbar_new_capture_successful));
        }
    }

    protected void finishCaptureAfterError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        sendResult(RESULT_CANCELED,
                getResources().getString(R.string.snackbar_new_capture_unsuccessful));
    }

    protected void sendResult(int resultCode, String resultString) {
        this.sensorManager.unregisterListener(magneticSensorListener);

        // The IOException here is always caught in DataWriter, with log lines written
        this.writer.close();

        Intent result = new Intent();
        result.putExtra("resultString", resultString);
        setResult(resultCode, result);
        finish();
    }
}

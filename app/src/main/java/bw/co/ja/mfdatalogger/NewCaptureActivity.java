package bw.co.ja.mfdatalogger;

import android.content.Intent;
import android.hardware.SensorManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class NewCaptureActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner floorsSpinner;
    private Spinner quadrantsSpinner;
    private Spinner frequenciesSpinner;
    private EditText roomInput;
    private EditText locationDetailInput;
    private EditText activityInput;
    private EditText durationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_capture);

        // Set up floor/quadrant spinners
        floorsSpinner = (Spinner) findViewById(R.id.spinner_floor);
        quadrantsSpinner = (Spinner) findViewById(R.id.spinner_quadrant);
        frequenciesSpinner = (Spinner) findViewById(R.id.spinner_frequency);

        ArrayAdapter<CharSequence> floorsAdapter = ArrayAdapter.createFromResource(this,
                R.array.diamond_floors, android.R.layout.simple_spinner_item);
        floorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorsSpinner.setAdapter(floorsAdapter);

        ArrayAdapter<CharSequence> quadrantsAdapter = ArrayAdapter.createFromResource(this,
                R.array.diamond_quadrants, android.R.layout.simple_spinner_item);
        quadrantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quadrantsSpinner.setAdapter(quadrantsAdapter);

        ArrayAdapter<CharSequence> frequenciesAdapter = ArrayAdapter.createFromResource(this,
                R.array.sample_frequencies, android.R.layout.simple_spinner_item);
        frequenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequenciesSpinner.setAdapter(frequenciesAdapter);

        // Set up button
        Button buttonStart = (Button) findViewById(R.id.button_new_capture_start);
        buttonStart.setOnClickListener(this);

        // Collect text inputs
        roomInput = (EditText) findViewById(R.id.input_room);
        locationDetailInput = (EditText) findViewById(R.id.input_location_detail);
        activityInput = (EditText) findViewById(R.id.input_local_activity);
        durationInput = (EditText) findViewById(R.id.input_new_capture_minutes);

        // Set to defaults
        resetInputs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.extra, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_captures_list) {
            // Open the list of captures
            Intent listIntent = new Intent(this, CaptureListActivity.class);
            startActivity(listIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.button_new_capture_start) { return; }
        if (!validateNewCaptureDetails()) { return; }

        // Input data
        CaptureDetails details = new CaptureDetails(
            floorsSpinner.getSelectedItem().toString(),
            quadrantsSpinner.getSelectedItem().toString(),
            Utils.viewToString(roomInput).replaceAll("\\s", ""),
            Utils.viewToString(locationDetailInput),
            Utils.viewToString(activityInput),
            getDuration(),
            frequenciesSpinner.getSelectedItem().toString()
        );

        // Start InProgressActivity or something
        Intent inProgressIntent = new Intent(this, InProgressActivity.class);
        inProgressIntent.putExtra("details", details);
        startActivityForResult(inProgressIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent requestData) {
        if (resultCode == RESULT_OK) {
            Snackbar.make(findViewById(R.id.layout_new_capture),
                    R.string.snackbar_new_capture_successful, Snackbar.LENGTH_LONG).show();

            resetInputs();
            roomInput.requestFocus();
        }
        else {
            Snackbar.make(findViewById(R.id.layout_new_capture),
                    R.string.snackbar_new_capture_unsuccessful, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    protected void resetInputs() {
        floorsSpinner.setSelection(0);
        quadrantsSpinner.setSelection(0);
        frequenciesSpinner.setSelection(1);
        roomInput.setText("");
        locationDetailInput.setText("");
        activityInput.setText("");
        durationInput.setText("");
    }

    protected boolean validateNewCaptureDetails() {
        CharSequence emptyError = getResources().getText(R.string.error_empty);

        if (Utils.textIsEmpty(roomInput)) {
            roomInput.setError(emptyError);
            return false;
        }

        if (Utils.textIsEmpty(locationDetailInput)) {
            locationDetailInput.setError(emptyError);
            return false;
        }

        if (Utils.textIsEmpty(activityInput)) {
            activityInput.setError(emptyError);
            return false;
        }

        return true;
    }

    /**
     * If there is no duration input, uses hint
     * @return int duration as minutes
     */
    protected int getDuration() {
        try {
            return Utils.viewToInt(this.durationInput);
        }
        catch (NumberFormatException ex) {
            return Integer.parseInt(durationInput.getHint().toString());
        }
    }
}

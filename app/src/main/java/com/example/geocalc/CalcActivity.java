package com.example.geocalc;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.geocalc.dummy.HistoryContent;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalcActivity extends AppCompatActivity {

    String distanceUnits = "Kilometers";
    String bearingUnits = "Degrees";
    public static int SETTINGS_REQUEST = 1;
    public static int HISTORY_REQUEST = 2;
    private static int SEARCH_REQUEST = 313;
    EditText p1Lat;
    EditText p2Lat;
    EditText p1Long;
    EditText p2Long;
    TextView distanceText;
    TextView bearingText;
    @BindView(R.id.searchButtton) Button searchButton;

    public void compute (){
        Intent payload = getIntent();
        if (payload.hasExtra("distanceUnits")) {
            distanceUnits = payload.getStringExtra("distanceUnits");
        }
        if (payload.hasExtra("bearingUnits")) {
            bearingUnits = payload.getStringExtra("bearingUnits");
        }

        try{
            hideKeyboard(CalcActivity.this);

            Double p1LatVal = Double.parseDouble(p1Lat.getText().toString());
            Double p1LongVal = Double.parseDouble(p2Lat.getText().toString());
            Double p2LatVal = Double.parseDouble(p1Long.getText().toString());
            Double p2LongVal = Double.parseDouble(p2Long.getText().toString());

            Location p1 = new Location(""); p1.setLatitude(p1LatVal); p1.setLongitude(p1LongVal);
            Location p2 = new Location(""); p2.setLatitude(p2LatVal); p2.setLongitude(p2LongVal);

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            float distance = p1.distanceTo(p2)/1000;
            Double d = (double) distance;

            if (distanceUnits.equals("Miles")) {
                d = d * 0.621371;
            }

            float bearing = p1.bearingTo(p2);
            Double b = (double) bearing;

            if (bearingUnits.equals("Mils")) {
                b = b * 17.777777777778;
            }

            distanceText.setText("Distance: " + df.format(d) + " " + distanceUnits);
            bearingText.setText("Bearing: " + df.format(b) + " " + bearingUnits);
            // Maybe check if we should make history?
            HistoryContent.HistoryItem item = new HistoryContent.HistoryItem(p1LatVal.toString(),
                    p1LongVal.toString(), p2LatVal.toString(), p2LongVal.toString(), DateTime.now());
            HistoryContent.addItem(item);

        }
        catch(Exception e){
            System.out.println("Need all numbers to calc");
        }
    }

    @OnClick(R.id.searchButtton)
    public void search() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Button calculate = findViewById(R.id.calcButton);
        Button clear = findViewById(R.id.clearButton);
        p1Lat = findViewById(R.id.p1Lat);
        p2Lat = findViewById(R.id.p1Long);
        p1Long = findViewById(R.id.p2Lat);
        p2Long = findViewById(R.id.p2Long);
        distanceText = findViewById(R.id.distanceText);
        bearingText = findViewById(R.id.bearingText);

        calculate.setOnClickListener(x -> {
            compute();
        });

        clear.setOnClickListener(x -> {
            hideKeyboard(CalcActivity.this);
            p1Lat.setText(""); p1Long.setText("");
            p2Lat.setText(""); p2Long.setText("");
            bearingText.setText("Bearing: ");
            distanceText.setText("Distance: ");

        });


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("No op");
        if (requestCode == SETTINGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.bearingUnits = data.getStringExtra("bearingUnits");
                this.distanceUnits = data.getStringExtra("distanceUnits");
                this.compute();
            }
        } else if (requestCode == HISTORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                String[] vals = data.getStringArrayExtra("item");
                this.p1Lat.setText(vals[0]);
                this.p1Long.setText(vals[1]);
                this.p2Lat.setText(vals[2]);
                this.p2Long.setText(vals[3]);
                this.compute();  // code that updates the calcs.
            }
        } else if (requestCode == SEARCH_REQUEST) {
            if (data != null && data.hasExtra("LOCATION_LOOKUP")) {
                if (resultCode == RESULT_OK) {
                    Parcelable par = data.getParcelableExtra("LOCATION_LOOKUP");
                    LocationLookup lu = Parcels.unwrap(par);
                    System.out.println(lu.toString());
                    this.p1Lat.setText(Double.toString(lu.origLat));
                    this.p1Long.setText(Double.toString(lu.origLng));
                    this.p2Lat.setText(Double.toString(lu.endLat));
                    this.p2Long.setText(Double.toString(lu.endLng));
                    this.compute();
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMenu) {
            Intent intent = new Intent(CalcActivity.this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
            return true;
        } else if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(CalcActivity.this, HistoryActivity.class);
            startActivityForResult(intent, HISTORY_REQUEST);
            return true;
        }

        return false;
    }

}


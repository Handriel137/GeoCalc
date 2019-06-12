package com.example.geocalc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geocalc.webservice.WeatherService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.geocalc.webservice.WeatherService.BROADCAST_WEATHER;

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
    @BindView(R.id.originWeather) ImageView originWeather;
    @BindView(R.id.destinationWeather) ImageView destinationWeather;
    @BindView(R.id.originTemp) TextView originTemp;
    @BindView(R.id.origWeatherDesc) TextView origWeatherDesc;
    @BindView(R.id.destTemp) TextView destTemp;
    @BindView(R.id.destWeatherDesc) TextView destWeatherDesc;
    DatabaseReference topRef;
    public static List<LocationLookup> allHistory;
    @BindView(R.id.searchButtton) Button searchButton;



    @Override
    public void onResume(){
        super.onResume();
        allHistory.clear();
        topRef = FirebaseDatabase.getInstance().getReference("history");
        topRef.addChildEventListener (chEvListener);
        IntentFilter weatherFilter = new IntentFilter(BROADCAST_WEATHER);
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, weatherFilter);
        setWeatherViews(View.INVISIBLE);
    }

    @Override
    public void onPause(){
        super.onPause();
        topRef.removeEventListener(chEvListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }


    private void setWeatherViews(int visible) {
        originWeather.setVisibility(visible);
        destinationWeather.setVisibility(visible);
        originTemp.setVisibility(visible);
        origWeatherDesc.setVisibility(visible);
        destTemp.setVisibility(visible);
        destWeatherDesc.setVisibility(visible);
    }

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

            WeatherService.startGetWeather(this, Double.toString(p1LatVal), Double.toString(p1LongVal), "p1");
            WeatherService.startGetWeather(this, Double.toString(p2LatVal), Double.toString(p2LongVal), "p2");

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

            LocationLookup entry = new LocationLookup();
            entry.setOrigLat(p1LatVal);
            entry.setOrigLng(p1LongVal);
            entry.setEndLat(p2LatVal);
            entry.setEndLng(p2LongVal);
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            entry.setTimestamp(fmt.print(DateTime.now()));
            topRef.push().setValue(entry);

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
        allHistory = new ArrayList<LocationLookup>();

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
            setWeatherViews(View.INVISIBLE);

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

    private ChildEventListener chEvListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            entry._key = dataSnapshot.getKey();
            allHistory.add(entry);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            List<LocationLookup> newHistory = new ArrayList<LocationLookup>();
            for (LocationLookup t : allHistory) {
                if (!t._key.equals(dataSnapshot.getKey())) {
                    newHistory.add(t);
                }
            }
            allHistory = newHistory;
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("WeatherBroadcast", "onReceive: " + intent);
            Bundle bundle = intent.getExtras();
            double temp = bundle.getDouble("TEMPERATURE");
            String summary = bundle.getString("SUMMARY");
            String icon = bundle.getString("ICON").replaceAll("-", "_");
            String key = bundle.getString("KEY");
            int resID = getResources().getIdentifier(icon , "drawable", getPackageName());
            setWeatherViews(View.VISIBLE);
            if (key.equals("p1"))  {
                origWeatherDesc.setText(summary);
                originTemp.setText(Double.toString(temp));
                originWeather.setImageResource(resID);
                originWeather.setVisibility(View.INVISIBLE);
            } else {
                destWeatherDesc.setText(summary);
                destTemp.setText(Double.toString(temp));
                destinationWeather.setImageResource(resID);
            }
        }
    };

}


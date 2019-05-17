package com.example.geocalc;

import android.app.Activity;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.location.Location;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calculate = (Button) findViewById(R.id.calcButton);
        Button clear= (Button) findViewById(R.id.clearButton);
        EditText p1Lat = (EditText) findViewById(R.id.p1Lat);
        EditText p2Lat = (EditText) findViewById(R.id.p1Long);
        EditText p1Long = (EditText) findViewById(R.id.p2Lat);
        EditText p2Long = (EditText) findViewById(R.id.p2Long);
        TextView distanceText = (TextView) findViewById(R.id.distanceText);
        TextView bearingText = (TextView) findViewById(R.id.bearingText);

        calculate.setOnClickListener(x -> {
            //todo check if empty
            try{
                hideKeyboard(MainActivity.this);

                Double p1LatVal = Double.parseDouble(p1Lat.getText().toString());
                Double p1LongVal = Double.parseDouble(p2Lat.getText().toString());
                Double p2LatVal = Double.parseDouble(p1Long.getText().toString());
                Double p2LongVal = Double.parseDouble(p2Long.getText().toString());
//            System.out.println(p1LatVal + p1LongVal + p2LatVal + p2LongVal);

                Location p1 = new Location("");
                Location p2 = new Location("");

                p1.setLatitude(p1LatVal);
                p1.setLongitude(p1LongVal);
                p2.setLatitude(p2LatVal);
                p2.setLongitude(p2LongVal);

                float distance = p1.distanceTo(p2)/1000;
                float bearing = p1.bearingTo(p2);

                distanceText.setText(Float.toString(distance));
                bearingText.setText(Float.toString(bearing));

            }
            catch(Exception e){
                System.out.println("Need all numbers to calc");
            }
        });

        clear.setOnClickListener(x -> {

            hideKeyboard(MainActivity.this);
            p1Lat.setText("");
            p1Long.setText("");
            p2Lat.setText("");
            p2Long.setText("");
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

}


package com.example.geocalc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    private static final int STARTING_LOCATION_REQUEST_CODE = 0;
    private static final int ENDING_LOCATION_REQUEST_CODE = 1;
    @BindView(R.id.starting_location) TextView startingLocation;
    @BindView(R.id.ending_location) TextView endingLocation;
    @BindView(R.id.calc_date) TextView calcDate;
    @BindView(R.id.fab) FloatingActionButton fab;
    private LocationLookup lookup;
    private static final String TAG = "NewLocationLookup";
    private FragmentManager fm = getSupportFragmentManager();



    @OnClick(R.id.starting_location)
    public void setStartingLocation() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG);
        Intent intent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
        startActivityForResult(intent, STARTING_LOCATION_REQUEST_CODE);
    }

    @OnClick(R.id.ending_location)
    public void setEndingLocation() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG);
        Intent intent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
        startActivityForResult(intent, ENDING_LOCATION_REQUEST_CODE);
    }

    @OnClick(R.id.calc_date)
    public void setCalcDate() {
        System.out.println("Set calc date button pushed");
        View v = findViewById(android.R.id.content);
        showDatePickerDialog(v);
    }

    @OnClick(R.id.fab)
    public void accept(){
        System.out.println(lookup.toString());
        Intent intent = new Intent();

        Parcelable parcel = Parcels.wrap(lookup);
        intent.putExtra("LOCATION_LOOKUP", parcel);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(fm, "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.w("DatePicker","Date = " + year);
        calcDate.setText(month + "/" + day + "/" + year);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STARTING_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place pl = Autocomplete.getPlaceFromIntent(data);
                startingLocation.setText(pl.getName());
                lookup.origLat = pl.getLatLng().latitude;
                lookup.origLng = pl.getLatLng().longitude;

                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status stat = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: ");
            } else if (requestCode == RESULT_CANCELED) {
                System.out.println("Cancelled by the user");
            }
        } else if (requestCode == ENDING_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place pl = Autocomplete.getPlaceFromIntent(data);
                endingLocation.setText(pl.getName());
                lookup.endLat = pl.getLatLng().latitude;
                lookup.endLng = pl.getLatLng().longitude;

                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status stat = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: ");
            } else if (requestCode == RESULT_CANCELED) {
                System.out.println("Cancelled by the user");
            }
        }

        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        lookup = new LocationLookup();
        Places.initialize(getApplicationContext(), "PLACES_KEY");
        ButterKnife.bind(this);
    }
}

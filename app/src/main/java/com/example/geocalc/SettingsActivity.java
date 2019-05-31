package com.example.geocalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner bearingSpinner = findViewById(R.id.bearingSpinner);
        Spinner distanceSpinner = findViewById(R.id.distanceSpinner);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, CalcActivity.class);
                String distanceChoice = distanceSpinner.getSelectedItem().toString();
                String bearingChoice = bearingSpinner.getSelectedItem().toString();
                intent.putExtra("distanceUnits", distanceChoice);
                intent.putExtra("bearingUnits", bearingChoice);
                setResult(1,intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

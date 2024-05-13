package com.example.exploro;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class TripResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_result);

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        int numberOfDays = intent.getIntExtra("numberOfDays", 0);
        ArrayList<String> selectedAttractions = intent.getStringArrayListExtra("selectedAttractions");

    }
}
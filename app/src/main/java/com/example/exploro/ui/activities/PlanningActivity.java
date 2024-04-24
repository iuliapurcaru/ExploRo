package com.example.exploro.ui.activities;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.databinding.ActivityPlanningBinding;
import com.example.exploro.domain.PlanningManager;
import com.example.exploro.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class PlanningActivity extends AppCompatActivity {

    private final List<String> selectedAttractions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.exploro.R.layout.activity_planning);

        ActivityPlanningBinding binding = ActivityPlanningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");

        final EditText startDateEditText = binding.editStartDate;
        final EditText endDateEditText = binding.editEndDate;
        final EditText numberOfAdultsEditText = binding.numAdults;
        final EditText numberOfStudentsEditText = binding.numStudents;
        final Button continueButton = binding.continueButton;

        startDateEditText.setOnClickListener(v -> PlanningManager.showDatePicker(startDateEditText, this));
        endDateEditText.setOnClickListener(v -> PlanningManager.showDatePicker(endDateEditText, this));

        continueButton.setOnClickListener(v -> {

            if (checkValidity(startDateEditText, endDateEditText, numberOfAdultsEditText, numberOfStudentsEditText, selectedAttractions)) {
                int numberOfDays = PlanningManager.calculateNumberOfDays(startDateEditText.getText().toString(), endDateEditText.getText().toString());
                Intent intentItinerary = new Intent(PlanningActivity.this, ItineraryActivity.class);
                Trip trip = new Trip("",
                        destinationID,
                        startDateEditText.getText().toString(),
                        endDateEditText.getText().toString(),
                        numberOfDays,
                        Integer.parseInt(numberOfAdultsEditText.getText().toString()),
                        Integer.parseInt(numberOfStudentsEditText.getText().toString()),
                        selectedAttractions);
                intentItinerary.putExtra("trip", trip);
                startActivity(intentItinerary);
            }
        });

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PlanningManager.displayAttractions(destinationID, selectedAttractions, binding, recyclerView);
    }

    private boolean checkValidity(EditText startDateEditText, EditText endDateEditText, EditText numberOfAdultsEditText, EditText numberOfStudentsEditText, List<String> selectedAttractions) {
        boolean isValid = true;

        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        if (startDate.isEmpty()) {
            startDateEditText.setError("Start date is required!");
            isValid = false;
        }

        if (endDate.isEmpty()) {
            endDateEditText.setError("End date is required!");
            isValid = false;
        }

        if (!startDate.isEmpty() && !endDate.isEmpty() && !PlanningManager.checkDates(startDate, endDate)) {
            endDateEditText.setError("End date must be after Start date!");
            isValid = false;
        }

        if (numberOfAdultsEditText.getText().toString().isEmpty()) {
            numberOfAdultsEditText.setError("Number of adults is required!");
            isValid = false;
        }

        if (numberOfStudentsEditText.getText().toString().isEmpty()) {
            numberOfStudentsEditText.setError("Number of students is required!");
            isValid = false;
        }

        if (selectedAttractions.isEmpty()) {
            Toast.makeText(PlanningActivity.this, "Please select at least one attraction!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }
}

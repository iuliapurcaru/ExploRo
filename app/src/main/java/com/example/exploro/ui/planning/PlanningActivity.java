package com.example.exploro.ui.planning;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.databinding.ActivityPlanningBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.R;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlanningActivity extends AppCompatActivity {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private final List<String> attractionsNames = new ArrayList<>();
    private final List<String> attractionsIDs = new ArrayList<>();
    private final List<String> selectedAttractions = new ArrayList<>();
    private RecyclerView recyclerView;
    private int numberOfDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.exploro.R.layout.activity_planning);

        ActivityPlanningBinding binding = ActivityPlanningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");

        startDateEditText = binding.editStartDate;
        endDateEditText = binding.editEndDate;
        final Button confirmButton = binding.continueButton;

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        confirmButton.setOnClickListener(v -> {
            if (startDateEditText.getText().toString().isEmpty() || endDateEditText.getText().toString().isEmpty()) {
                if (startDateEditText.getText().toString().isEmpty()) { //TODO: See why error message is not displayed
                    startDateEditText.setError("Start date is required!");
                }
                if (endDateEditText.getText().toString().isEmpty()) {
                    endDateEditText.setError("End date is required!");
                }
            } else if (!checkDates()) {
                endDateEditText.setError("End date must be after Start date!");
            } else if (selectedAttractions.isEmpty()) {
                Toast.makeText(PlanningActivity.this, "Please select at least one attraction!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intentTrip = new Intent(PlanningActivity.this, ItineraryActivity.class);
                intentTrip.putExtra("destination", destinationID);
                intentTrip.putStringArrayListExtra("selectedAttractions", (ArrayList<String>) selectedAttractions);
                intentTrip.putExtra("startDate", startDateEditText.getText().toString());
                intentTrip.putExtra("endDate", endDateEditText.getText().toString());
                intentTrip.putExtra("numberOfDays", numberOfDays);
                startActivity(intentTrip);
            }
        });

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAttractionsNames(destinationID);
    }

    public void showDatePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PlanningActivity.this,
                (datePicker, year1, month1, day) -> {
                    String selectedDate = day + "/" + (month1 + 1) + "/" + year1;
                    editText.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public boolean checkDates() {
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        if (!startDateStr.isEmpty() && !endDateStr.isEmpty()) {
            String[] startDateParts = startDateStr.split("/");
            String[] endDateParts = endDateStr.split("/");

            int startDay = Integer.parseInt(startDateParts[0]);
            int startMonth = Integer.parseInt(startDateParts[1]);
            int startYear = Integer.parseInt(startDateParts[2]);

            int endDay = Integer.parseInt(endDateParts[0]);
            int endMonth = Integer.parseInt(endDateParts[1]);
            int endYear = Integer.parseInt(endDateParts[2]);

            numberOfDays = endDay - startDay + 1;

            return endYear >= startYear &&
                    (endYear != startYear || endMonth >= startMonth) &&
                    (endYear != startYear || endMonth != startMonth || endDay >= startDay);
        }
        return false;
    }

    private void getAttractionsNames(String destinationID) {

        DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference("attractions/" + destinationID);
        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String attractionID = snapshot.getKey();
                        attractionsIDs.add(attractionID);
                        String attractionName = snapshot.child("name").getValue(String.class);
                        attractionsNames.add(attractionName);
                    }
                    PlanningAdapter adapter = new PlanningAdapter(attractionsNames, selectedAttractions, findViewById(R.id.overlay), destinationID, attractionsIDs);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }
}
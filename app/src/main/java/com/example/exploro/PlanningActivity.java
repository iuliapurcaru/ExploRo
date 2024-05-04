package com.example.exploro;

import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.databinding.ActivityPlanningBinding;

import java.util.Calendar;

public class PlanningActivity extends AppCompatActivity {

    private EditText startDateEditText;
    private EditText endDateEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        ActivityPlanningBinding binding = ActivityPlanningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startDateEditText = binding.editStartDate;
        endDateEditText = binding.editEndDate;

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));
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

    public void checkDates(View view) {
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

            if (endYear < startYear ||
                    (endYear == startYear && endMonth < startMonth) ||
                    (endYear == startYear && endMonth == startMonth && endDay < startDay)) {
                Log.d("PlanningActivity", "End date is earlier than start date");
            } else {
                Log.d("PlanningActivity", "End date is not earlier than start date");
            }
        } else {
            Log.d("PlanningActivity", "Please select both start and end dates");
        }
    }
}
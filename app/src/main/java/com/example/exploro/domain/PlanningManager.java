package com.example.exploro.domain;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlanningManager {

    private static final String TAG = PlanningManager.class.getSimpleName();

    public static void showDatePicker(EditText editText, Activity activity) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (datePicker, year1, month1, day) -> {
            String selectedDate = day + "/" + (month1 + 1) + "/" + year1;
            editText.setText(selectedDate);
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public static int calculateNumberOfDays(String startDate, String endDate) {
        String[] startDateParts = startDate.split("/");
        String[] endDateParts = endDate.split("/");
        int startDay = Integer.parseInt(startDateParts[0]);
        int endDay = Integer.parseInt(endDateParts[0]);

        return endDay - startDay + 1;
    }

    public static boolean checkDates(String startDate, String endDate) {

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            String[] startDateParts = startDate.split("/");
            String[] endDateParts = endDate.split("/");

            int startDay = Integer.parseInt(startDateParts[0]);
            int startMonth = Integer.parseInt(startDateParts[1]);
            int startYear = Integer.parseInt(startDateParts[2]);

            int endDay = Integer.parseInt(endDateParts[0]);
            int endMonth = Integer.parseInt(endDateParts[1]);
            int endYear = Integer.parseInt(endDateParts[2]);

            return endYear >= startYear &&
                    (endYear != startYear || endMonth >= startMonth) &&
                    (endYear != startYear || endMonth != startMonth || endDay >= startDay);
        }
        return false;
    }

    public static Calendar parseStartDate(String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date startDateParsed = dateFormat.parse(startDate);
            if (startDateParsed != null) {
                calendar.setTime(startDateParsed);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing start date", e);
        }
        return calendar;
    }

}

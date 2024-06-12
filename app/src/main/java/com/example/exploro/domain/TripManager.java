package com.example.exploro.domain;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.models.Attraction;
import com.example.exploro.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripManager {

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static final String TAG = TripManager.class.getSimpleName();

    public static void saveTrip(Activity activity, Trip trip, List<Attraction> selectedAttractions) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());
            String tripID = userReference.child("trips").push().getKey();
            if (tripID == null) {
                Toast.makeText(activity, "Failed to generate trip ID", Toast.LENGTH_SHORT).show();
                return;
            }
            if (trip != null) {
                String destinationID = trip.getDestinationID();
                String startDate = trip.getStartDate();
                String endDate = trip.getEndDate();
                int numberOfDays = trip.getNumberOfDays();
                int numberOfAdults = trip.getNumberOfAdults();
                int numberOfStudents = trip.getNumberOfStudents();

                Map<String, Object> tripPlan = new HashMap<>();
                tripPlan.put("destination_id", destinationID);
                tripPlan.put("start_date", startDate);
                tripPlan.put("end_date", endDate);
                tripPlan.put("number_days", numberOfDays);
                tripPlan.put("number_adults", numberOfAdults);
                tripPlan.put("number_students", numberOfStudents);

                List<String> attractionsNames = new ArrayList<>();
                for (Attraction attraction : selectedAttractions) {
                    attractionsNames.add(attraction.getId());
                }
                tripPlan.put("attractions", attractionsNames);

                userReference.child("trips").child(tripID).setValue(tripPlan)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity, "Trip plan saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Failed to save trip plan.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public static void deleteTrip(String tripID, int position, RecyclerView.Adapter<?> adapter) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference tripReference = FirebaseDatabase.getInstance().getReference("users/" + mUser.getUid() + "/trips/" + tripID);

        tripReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (position != RecyclerView.NO_POSITION) {
                    adapter.notifyItemRemoved(position);
                } else {
                    Log.w(TAG, "Attempted to remove item at invalid RecyclerView position: " + position);
                }
            } else {
                Log.w(TAG, "Failed to delete trip.", task.getException());
            }
        });
    }

}

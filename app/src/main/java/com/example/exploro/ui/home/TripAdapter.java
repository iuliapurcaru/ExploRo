package com.example.exploro.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.R;
import com.example.exploro.TripInfo;
import com.example.exploro.ui.planning.ItineraryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<TripInfo> tripList;

    public TripAdapter(List<TripInfo> tripList) {
        this.tripList = tripList;
    }

    public void setTrips(List<TripInfo> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripInfo tripInfo = tripList.get(position);
        holder.textStartDate.setText(tripInfo.getStartDate());
        holder.textEndDate.setText(tripInfo.getEndDate());

        DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference("destinations/" + tripInfo.getDestinationID());
        destinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // TODO get destination name
                    String destination = dataSnapshot.child("text").getValue(String.class);
                    holder.textDestination.setText(destination);
                } else {
                    Log.w("DATABASE", "Failed to get destination data.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DATABASE", "Failed to get database data.", databaseError.toException());
            }
        });

        holder.buttonShowTrip.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItineraryActivity.class);
            intent.putExtra("tripInfo", tripInfo);
            v.getContext().startActivity(intent);
        });

        // TODO: Implement delete trip functionality to immediately delete a trip from the interface
        holder.buttonDeleteTrip.setOnClickListener(v -> {
            DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/trips/" + tripInfo.getTripID());
            tripRef.removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        final TextView textDestination;
        final TextView textStartDate;
        final TextView textEndDate;
        final TextView buttonShowTrip;
        final TextView buttonDeleteTrip;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            textDestination = itemView.findViewById(R.id.textDestination);
            textStartDate = itemView.findViewById(R.id.textStartDate);
            textEndDate = itemView.findViewById(R.id.textEndDate);
            buttonShowTrip = itemView.findViewById(R.id.showTrip);
            buttonDeleteTrip = itemView.findViewById(R.id.deleteTrip);
        }
    }
}

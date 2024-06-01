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
import com.example.exploro.ui.PopupMenu;
import com.example.exploro.ui.planning.ItineraryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<TripInfo> tripList;
    private final View overlay;

    public TripAdapter(List<TripInfo> tripList, View overlay) {
        this.tripList = tripList;
        this.overlay = overlay;
    }

    public void setTrips(List<TripInfo> tripList) {
        this.tripList = tripList;
        notifyDataSetChanged();
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

        holder.buttonDeleteTrip.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            PopupMenu.showDeleteTripPopup(v.getContext(), holder.buttonDeleteTrip, () -> overlay.setVisibility(View.GONE), tripInfo, TripAdapter.this, position);
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void removeTrip(int position) {
        if (position >= 0 && position < tripList.size()) {
            tripList.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.w("TripAdapter", "Attempted to remove item at invalid position: " + position);
        }
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

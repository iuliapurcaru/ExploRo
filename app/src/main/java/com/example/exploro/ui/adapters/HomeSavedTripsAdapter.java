package com.example.exploro.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.R;
import com.example.exploro.data.models.Trip;
import com.example.exploro.data.repositories.DestinationRemoteDataSource;
import com.example.exploro.utils.PopupMenu;
import com.example.exploro.ui.activities.ItineraryActivity;

import java.util.List;

public class HomeSavedTripsAdapter extends RecyclerView.Adapter<HomeSavedTripsAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private final View overlay;

    public HomeSavedTripsAdapter(List<Trip> tripList, View overlay) {
        this.tripList = tripList;
        this.overlay = overlay;
    }

    public void setTrips(List<Trip> tripList) {
        this.tripList = tripList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_home_trips, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.textStartDate.setText(trip.getStartDate());
        holder.textEndDate.setText(trip.getEndDate());

        DestinationRemoteDataSource.fetchDestinationName(trip.getDestinationID(), null, holder);

        holder.buttonShowTrip.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItineraryActivity.class);
            intent.putExtra("trip", trip);
            v.getContext().startActivity(intent);
        });

        holder.buttonDeleteTrip.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            PopupMenu.showDeleteTripPopup(v.getContext(), holder.buttonDeleteTrip, () -> overlay.setVisibility(View.GONE), trip, HomeSavedTripsAdapter.this, position);
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        public final TextView textDestination;
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

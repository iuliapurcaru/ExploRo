package com.example.exploro.ui.planning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.AttractionInfo;
import com.example.exploro.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {

    private final List<List<AttractionInfo>> itinerary;

    public ItineraryAdapter(List<List<AttractionInfo>> itinerary) {
        this.itinerary = itinerary;
    }

    @NotNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_itinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        List<AttractionInfo> dayPlan = itinerary.get(position);
        holder.bind(dayPlan, position);
    }

    @Override
    public int getItemCount() {
        return itinerary.size();
    }

    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        final TextView dayTitleTextView;
        final TextView attractionsTextView;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTitleTextView = itemView.findViewById(R.id.dayTextView);
            attractionsTextView = itemView.findViewById(R.id.attractionsTextView);
        }

        public void bind(List<AttractionInfo> dayPlan, int dayIndex) {
            String currentDay = "Day " + (dayIndex + 1);
            dayTitleTextView.setText(currentDay);
            StringBuilder attractionsStringBuilder = new StringBuilder();
            for (AttractionInfo attraction : dayPlan) {
                attractionsStringBuilder.append(attraction.getName())
                        .append(" - ")
                        .append(attraction.getVisitTime())
                        .append("\n");
            }
            attractionsTextView.setText(attractionsStringBuilder.toString());
        }
    }
}

package com.example.exploro.ui.planning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.AttractionInfo;
import com.example.exploro.R;
import com.example.exploro.TimeDistanceHandler;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {

    private final List<List<AttractionInfo>> itinerary;
    private final Calendar calendar;

    public ItineraryAdapter(List<List<AttractionInfo>> itinerary, String startDate) {
        this.itinerary = itinerary;
        this.calendar = TimeDistanceHandler.parseStartDate(startDate);
    }

    @NotNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_itinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int day) {
        List<AttractionInfo> dayPlan = itinerary.get(day);

        holder.bind(dayPlan, day, calendar);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    @Override
    public int getItemCount() {
        return itinerary.size();
    }

    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        final TextView dayTitleTextView;
        final TextView dateTextView;
        final TextView attractionsTextView;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTitleTextView = itemView.findViewById(R.id.dayTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            attractionsTextView = itemView.findViewById(R.id.attractionsTextView);
        }

        public void bind(List<AttractionInfo> dayPlan, int dayIndex, Calendar calendar) {
            String currentDay = "Day " + (dayIndex + 1);
            dayTitleTextView.setText(currentDay);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateTextView.setText(dateFormat.format(calendar.getTime()));

            StringBuilder attractionsStringBuilder = new StringBuilder();
            for (AttractionInfo attraction : dayPlan) {
                int hours = (int) attraction.getVisitTime();
                int minutes = (int) ((attraction.getVisitTime() - hours) * 60);
                String formattedVisitTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                attractionsStringBuilder.append(attraction.getName())
                        .append(" - ")
                        .append(formattedVisitTime)
                        .append("\n");
            }
            attractionsTextView.setText(attractionsStringBuilder.toString());
        }
    }
}

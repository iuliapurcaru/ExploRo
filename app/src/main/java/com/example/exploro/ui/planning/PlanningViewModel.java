package com.example.exploro.ui.planning;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.PopupMenu;
import com.example.exploro.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlanningViewModel extends RecyclerView.Adapter<PlanningViewModel.PlanningViewHolder> {

    private final List<String> attractionsNames;
    private final List<String> selectedItems;
    private final View overlay;

    public PlanningViewModel(List<String> attractionsNames, List<String> selectedItems, View overlay) {
        this.attractionsNames = attractionsNames;
        this.selectedItems = selectedItems;
        this.overlay = overlay;
    }

    @NotNull
    @Override
    public PlanningViewModel.PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_attractions, parent, false);
        return new PlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
        String item = attractionsNames.get(position);
        holder.attractionRadioButton.setText(item);

        holder.attractionRadioButton.setChecked(selectedItems.contains(item));
        holder.attractionRadioButton.setOnClickListener(v -> {
            if (holder.attractionRadioButton.isChecked()) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
        });

        holder.attractionDetails.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            PopupMenu.showAttractionDetailsPopup(v.getContext(), holder.attractionDetails, () -> overlay.setVisibility(View.GONE));
        });

    }

    @Override
    public int getItemCount() {
        return attractionsNames.size();
    }

    public static class PlanningViewHolder extends RecyclerView.ViewHolder {
        RadioButton attractionRadioButton;
        TextView attractionDetails;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionRadioButton = itemView.findViewById(R.id.attraction_name);
            attractionDetails = itemView.findViewById(R.id.details_button);
        }
    }
}
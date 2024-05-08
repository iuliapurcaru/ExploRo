package com.example.exploro.ui.planning;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private final String destinationID;
    private final List<String> attractionsIDs;

    public PlanningViewModel(List<String> attractionsNames, List<String> selectedItems, View overlay, String destinationID, List<String> attractionsIDs) {
        this.attractionsNames = attractionsNames;
        this.selectedItems = selectedItems;
        this.overlay = overlay;
        this.destinationID = destinationID;
        this.attractionsIDs = attractionsIDs;
    }

    @NotNull
    @Override
    public PlanningViewModel.PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_attractions, parent, false);
        return new PlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
        String itemName = attractionsNames.get(position);
        holder.attractionCheckBox.setText(itemName);

        holder.attractionCheckBox.setChecked(selectedItems.contains(itemName));
        holder.attractionCheckBox.setOnClickListener(v -> {
            if (holder.attractionCheckBox.isChecked()) {
                selectedItems.add(itemName);
            } else {
                selectedItems.remove(itemName);
            }
        });

        String itemID = attractionsIDs.get(position);

        holder.attractionDetails.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            PopupMenu.showAttractionDetailsPopup(v.getContext(), holder.attractionDetails, () -> overlay.setVisibility(View.GONE), destinationID, itemID);
        });

    }

    @Override
    public int getItemCount() {
        return attractionsNames.size();
    }

    public static class PlanningViewHolder extends RecyclerView.ViewHolder {
        CheckBox attractionCheckBox;
        TextView attractionDetails;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionCheckBox = itemView.findViewById(R.id.attraction_name);
            attractionDetails = itemView.findViewById(R.id.details_button);
        }
    }
}
package com.example.exploro.ui.planning;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.R;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class PlanningViewModel extends RecyclerView.Adapter<PlanningViewModel.PlanningViewHolder> {

    private final List<String> attractionsNames;
    //TODO: Add other info if necessary

    public PlanningViewModel(List<String> attractionsNames) {
        this.attractionsNames = attractionsNames;
    }

    @NotNull
    @Override
    public PlanningViewModel.PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_attractions, parent, false);
        return new PlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
//        Picasso.get().load(attractionsNames.get(position)).into(holder.imageViewPhoto);
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), PlanningActivity.class);
//            v.getContext().startActivity(intent);
//        });
        //TODO: Bind attractionsNames to the view
    }

    @Override
    public int getItemCount() {
        return attractionsNames.size();
    }

    public static class PlanningViewHolder extends RecyclerView.ViewHolder {
        TextView attractionName;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionName = itemView.findViewById(R.id.attraction_name);
        }
    }

}
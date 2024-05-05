package com.example.exploro.ui.explore;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.R;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import com.example.exploro.PlanningActivity;

public class ExploreViewModel extends RecyclerView.Adapter<ExploreViewModel.ExploreViewHolder> {

    private final List<String> destinationsImageUrls;

    public ExploreViewModel(List<String> destinationsImageUrls, List<String> destinationsTexts) {
        this.destinationsImageUrls = destinationsImageUrls;
    }

    @NotNull
    @Override
    public ExploreViewModel.ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_destinations, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        Picasso.get().load(destinationsImageUrls.get(position)).into(holder.imageViewPhoto);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PlanningActivity.class);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return destinationsImageUrls.size();
    }

    public static class ExploreViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageView);
        }
    }

}
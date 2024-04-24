package com.example.exploro.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exploro.domain.TripManager;
import com.example.exploro.domain.UserManager;
import com.example.exploro.models.Trip;
import com.example.exploro.databinding.FragmentHomeBinding;
import com.example.exploro.ui.adapters.HomeSavedTripsAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Trip> tripList = new ArrayList<>();
        HomeSavedTripsAdapter homeSavedTripsAdapter = new HomeSavedTripsAdapter(tripList, binding.overlay);

        final TextView textHome = binding.textHome;
        final TextView textViewTrips = binding.textTrips;

        UserManager.fetchDisplayName(textHome);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(homeSavedTripsAdapter);

        TripManager.fetchSavedTrips(textViewTrips, homeSavedTripsAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

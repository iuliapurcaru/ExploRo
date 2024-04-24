package com.example.exploro.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.databinding.FragmentExploreBinding;
import com.example.exploro.domain.ExploreManager;
import com.example.exploro.ui.adapters.ExploreDestinationsAdapter;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ExploreManager.displayDestinationsImageUrls(recyclerView);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
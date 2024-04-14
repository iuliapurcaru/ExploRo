package com.example.exploro.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exploro.LoginActivity;
import com.example.exploro.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    FirebaseAuth mAuth;
    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        final TextView textView = binding.textAccount;
        final Button logoutButton = binding.logout;
        final View progressBar = binding.loading;

        accountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        logoutButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
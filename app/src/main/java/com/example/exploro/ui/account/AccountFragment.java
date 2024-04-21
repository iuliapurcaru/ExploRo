package com.example.exploro.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exploro.LoginActivity;
import com.example.exploro.databinding.FragmentAccountBinding;
import com.example.exploro.ui.PopupMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    FirebaseAuth mAuth;
    private FragmentAccountBinding binding;
    private TextView changeCurrency;
    private View overlay;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        final TextView textAccount = binding.textAccount;
        final Button logoutButton = binding.logout;
        final View progressBar = binding.loading;
        changeCurrency = binding.changeCurrency;
        final TextView changePassword = binding.changePassword;
        overlay = binding.overlay;

        accountViewModel.getText().observe(getViewLifecycleOwner(), textAccount::setText);

        logoutButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        changeCurrency.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            showPopupMenu();
        });

        changePassword.setOnClickListener(v -> {
            FirebaseUser mUser = mAuth.getCurrentUser();
            assert mUser != null;
            changePassword(mUser);
            Toast.makeText(requireContext(), "Reset password email sent to " + mUser.getEmail(), Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showPopupMenu() {
        PopupMenu.showPopupCurrency(requireParentFragment(), changeCurrency, () -> overlay.setVisibility(View.GONE));
    }

    private void changePassword(FirebaseUser user) {
        String email = user.getEmail();
        mAuth = FirebaseAuth.getInstance();
        assert email != null;
        mAuth.sendPasswordResetEmail(email);
    }
}
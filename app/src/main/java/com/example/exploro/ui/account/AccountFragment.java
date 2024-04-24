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
import com.example.exploro.ui.PopupMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private FragmentAccountBinding binding;
    private TextView changeCurrency;
    private TextView resetPassword;
    private TextView editEmail;
    private TextView editDisplayName;
    private TextView deleteAccount;
    private View overlay;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        final TextView textAccount = binding.textAccount;
        final Button logoutButton = binding.logout;
        final View progressBar = binding.loading;
        overlay = binding.overlay;
        changeCurrency = binding.changeCurrency;
        resetPassword = binding.resetPassword;
        editEmail = binding.editEmail;
        editDisplayName = binding.editName;
        deleteAccount = binding.deleteAccount;

        accountViewModel.getDisplayNameText().observe(getViewLifecycleOwner(), textAccount::setText);

        logoutButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        changeCurrency.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            showChangeCurrencyPopupMenu();
        });

        resetPassword.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            resetPassword(mUser);
        });

        editEmail.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            editEmail(mUser);
        });

        editDisplayName.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            editDisplayName();
            accountViewModel.getDisplayNameText().observe(getViewLifecycleOwner(), textAccount::setText);
        });

        deleteAccount.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            deleteAccount();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void resetPassword(FirebaseUser user) {
        String email = user.getEmail();
        mAuth = FirebaseAuth.getInstance();
        assert email != null;
        mAuth.sendPasswordResetEmail(email);
        overlay.setVisibility(View.VISIBLE);
        showResetPasswordPopupMenu();
    }

    private void editEmail(FirebaseUser user) {
//        String email = user.getEmail();
//        mAuth = FirebaseAuth.getInstance();
//        assert email != null;
//        mAuth.send(email);
//        overlay.setVisibility(View.VISIBLE);
//        showResetPasswordPopupMenu();
    }

    private void editDisplayName() {
        overlay.setVisibility(View.VISIBLE);
        showEditDisplayNamePopupMenu();
    }

    private void deleteAccount() {
        overlay.setVisibility(View.VISIBLE);
        showDeleteAccountPopupMenu();
    }
    
    private void showChangeCurrencyPopupMenu() {
        PopupMenu.showPopupCurrency(requireParentFragment(), changeCurrency, () -> overlay.setVisibility(View.GONE));
    }

    private void showResetPasswordPopupMenu() {
        PopupMenu.showResetPasswordPopup(requireParentFragment(), resetPassword, () -> overlay.setVisibility(View.GONE));
    }

    private void showEditEmailPopupMenu() {
//        PopupMenu.showEditEmailPopup(requireParentFragment(), editEmail, () -> overlay.setVisibility(View.GONE));
    }

    private void showEditDisplayNamePopupMenu() {
        PopupMenu.showEditDisplayNamePopup(requireParentFragment(), editDisplayName, () -> overlay.setVisibility(View.GONE));
    }

    private void showDeleteAccountPopupMenu() {
        if (PopupMenu.showDeleteAccountPopup(requireParentFragment(), deleteAccount, () -> overlay.setVisibility(View.GONE))) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
package com.example.exploro.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import androidx.fragment.app.Fragment;
import com.example.exploro.ui.LoginActivity;
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
    private TextView editDisplayName;
    private TextView deleteAccount;
    private View overlay;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        editDisplayName = binding.editName;
        deleteAccount = binding.deleteAccount;

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");

        mNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String displayName = dataSnapshot.getValue(String.class);
                    textAccount.setText(displayName);
                } else {
                    textAccount.setText(" ");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

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

        editDisplayName.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            editDisplayName();
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

    private void showEditDisplayNamePopupMenu() {
        PopupMenu.showEditDisplayNamePopup(requireParentFragment(), editDisplayName, () -> overlay.setVisibility(View.GONE));
    }

    private void showDeleteAccountPopupMenu() {
        PopupMenu.showDeleteAccountPopup(requireParentFragment(), deleteAccount, () -> overlay.setVisibility(View.GONE));
    }
}
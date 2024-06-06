package com.example.exploro.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import com.example.exploro.domain.AccountManager;
import com.example.exploro.databinding.FragmentAccountBinding;
import com.example.exploro.utils.PopupMenu;
import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FragmentAccountBinding binding;
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
            AccountManager.signOut(this);
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
        AccountManager.resetPassword();
        overlay.setVisibility(View.VISIBLE);
        showResetPasswordPopupMenu(user);
    }

    private void editDisplayName() {
        overlay.setVisibility(View.VISIBLE);
        showEditDisplayNamePopupMenu();
    }

    private void deleteAccount() {
        overlay.setVisibility(View.VISIBLE);
        showDeleteAccountPopupMenu();
    }

    private void showResetPasswordPopupMenu(FirebaseUser user) {
        PopupMenu.showResetPasswordPopup(requireParentFragment(), resetPassword, () -> overlay.setVisibility(View.GONE), user.getEmail());
    }

    private void showEditDisplayNamePopupMenu() {
        PopupMenu.showEditDisplayNamePopup(requireParentFragment(), editDisplayName, () -> overlay.setVisibility(View.GONE));
    }

    private void showDeleteAccountPopupMenu() {
        PopupMenu.showDeleteAccountPopup(requireParentFragment(), deleteAccount, () -> overlay.setVisibility(View.GONE));
    }
}
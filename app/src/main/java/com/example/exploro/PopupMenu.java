package com.example.exploro;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exploro.ui.account.AccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class PopupMenu {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static FirebaseUser mUser;

    public static void showForgotPasswordPopup(LoginActivity activity, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_forgot_password, (ViewGroup) activity.getWindow().getDecorView(), false);

        final EditText emailEditText = popupView.findViewById(R.id.email);
        final Button resetPassButton = popupView.findViewById(R.id.reset_password);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        mAuth = FirebaseAuth.getInstance();

        resetPassButton.setOnClickListener(v -> {
            if (emailEditText.getText().toString().isEmpty()) {
                Toast.makeText(activity, "Please fill in email field!", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(emailEditText.getText().toString());
            Toast.makeText(activity, "Reset password email sent to " + emailEditText.getText().toString(), Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }

    public static void showPopupCurrency(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_change_currency, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        assert mUser != null;
        DatabaseReference mCurrencyReference = mDatabase.getReference("users/" + mUser.getUid() + "/currency/");

        RadioButton radioOptionRON = popupView.findViewById(R.id.radio_option1);
        RadioButton radioOptionEUR = popupView.findViewById(R.id.radio_option2);
        RadioButton radioOptionUSD = popupView.findViewById(R.id.radio_option3);
        final RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);

        mCurrencyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                String currencyValue = dataSnapshot.getValue(String.class);
                RadioButton selectedRadioButton = null;

                if (currencyValue != null) {
                    switch (currencyValue) {
                        case "RON":
                            selectedRadioButton = radioOptionRON;
                            break;
                        case "EUR":
                            selectedRadioButton = radioOptionEUR;
                            break;
                        case "USD":
                            selectedRadioButton = radioOptionUSD;
                            break;
                    }
                    assert selectedRadioButton != null;
                    selectedRadioButton.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedOption = "";
            if (checkedId == R.id.radio_option1) {
                selectedOption = "RON";
            } else if (checkedId == R.id.radio_option2) {
                selectedOption = "EUR";
            } else if (checkedId == R.id.radio_option3) {
                selectedOption = "USD";
            }

            mCurrencyReference.setValue(selectedOption);
        });

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showPopupDistance(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_change_distance, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        assert mUser != null;
        DatabaseReference mDistanceReference = mDatabase.getReference("users/" + mUser.getUid() + "/distance_unit/");

        RadioButton radioOptionKilometers = popupView.findViewById(R.id.radio_option1);
        RadioButton radioOptionMiles = popupView.findViewById(R.id.radio_option2);
        final RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);

        mDistanceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currencyValue = dataSnapshot.getValue(String.class);
                    RadioButton selectedRadioButton = null;

                    if (currencyValue != null) {
                        switch (currencyValue) {
                            case "kilometers (km)":
                                selectedRadioButton = radioOptionKilometers;
                                break;
                            case "miles (mi)":
                                selectedRadioButton = radioOptionMiles;
                                break;
                        }
                        assert selectedRadioButton != null;
                        selectedRadioButton.setChecked(true);
                    }
                }
                else {
                    Log.w("DATABASE", "Failed to get database data.");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedOption = "";
            if (checkedId == R.id.radio_option1) {
                selectedOption = "kilometers (km)";
            } else if (checkedId == R.id.radio_option2) {
                selectedOption = "miles (mi)";
            }

            mDistanceReference.setValue(selectedOption);
        });

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showResetPasswordPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_reset_password, (ViewGroup) fragment.getView(), false);

        AccountViewModel accountViewModel = new ViewModelProvider(fragment).get(AccountViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final TextView emailTextView = popupView.findViewById(R.id.reset_email);

        accountViewModel.getEmailText().observe(fragment.getViewLifecycleOwner(), emailTextView::setText);

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showEditDisplayNamePopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_edit_name, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final EditText displayNameEditText = popupView.findViewById(R.id.display_name);
        final Button confirmButton = popupView.findViewById(R.id.confirm_button);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");


        confirmButton.setOnClickListener(v -> {
            if (displayNameEditText.getText().toString().isEmpty()) {
                Toast.makeText(fragment.getContext(), "Please fill in name field!", Toast.LENGTH_SHORT).show();
                return;
            }
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayNameEditText.getText().toString())
                    .build();
            mUser.updateProfile(profileUpdates);
            Toast.makeText(fragment.getContext(), "Display name updated!", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            mNameReference.setValue(displayNameEditText.getText().toString());
        });
    }

    public static void showDeleteAccountPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_delete_account, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();

        final Button yesButton = popupView.findViewById(R.id.yes_button);
        final Button noButton = popupView.findViewById(R.id.no_button);

        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        noButton.setOnClickListener(v -> popupWindow.dismiss());

        yesButton.setOnClickListener(v -> {
            mDatabase = FirebaseDatabase.getInstance();
            mUser = mAuth.getCurrentUser();
            DatabaseReference mUserReference = mDatabase.getReference().child("users/" + mUser.getUid());
            assert mUser != null;
            mUser.delete();
            Toast.makeText(fragment.getContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            mAuth.signOut();
            Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
            fragment.startActivity(intent);
            fragment.requireActivity().finish();
            mUserReference.removeValue();
        });
    }

    private static PopupWindow configurePopupWindow(View anchorView, PopupWindow.OnDismissListener dismissListener, View popupView) {
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        if (dismissListener != null) {
            popupWindow.setOnDismissListener(dismissListener);
        }

        return popupWindow;
    }
}

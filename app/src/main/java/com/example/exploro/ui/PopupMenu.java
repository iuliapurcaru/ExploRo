package com.example.exploro.ui;

import android.content.Context;
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
import com.example.exploro.LoginActivity;
import com.example.exploro.R;
import com.example.exploro.ui.account.AccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PopupMenu {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static FirebaseUser mUser;

    public static void showForgotPasswordPopup(LoginActivity activity, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(activity).inflate(com.example.exploro.R.layout.popup_login_forgot_password, (ViewGroup) activity.getWindow().getDecorView(), false);

        final EditText emailEditText = popupView.findViewById(com.example.exploro.R.id.email);
        final Button resetPassButton = popupView.findViewById(com.example.exploro.R.id.reset_password);
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
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_change_currency, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        assert mUser != null;
        DatabaseReference mCurrencyReference = mDatabase.getReference("users/" + mUser.getUid() + "/currency/");

        RadioButton radioOptionRON = popupView.findViewById(com.example.exploro.R.id.radio_option1);
        RadioButton radioOptionEUR = popupView.findViewById(com.example.exploro.R.id.radio_option2);
        RadioButton radioOptionUSD = popupView.findViewById(com.example.exploro.R.id.radio_option3);
        final RadioGroup radioGroup = popupView.findViewById(com.example.exploro.R.id.radio_group);

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
            if (checkedId == com.example.exploro.R.id.radio_option1) {
                selectedOption = "RON";
            } else if (checkedId == com.example.exploro.R.id.radio_option2) {
                selectedOption = "EUR";
            } else if (checkedId == com.example.exploro.R.id.radio_option3) {
                selectedOption = "USD";
            }

            mCurrencyReference.setValue(selectedOption);
        });

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showPopupDistance(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_change_distance, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        assert mUser != null;
        DatabaseReference mDistanceReference = mDatabase.getReference("users/" + mUser.getUid() + "/distance_unit/");

        RadioButton radioOptionKilometers = popupView.findViewById(com.example.exploro.R.id.radio_option1);
        RadioButton radioOptionMiles = popupView.findViewById(com.example.exploro.R.id.radio_option2);
        final RadioGroup radioGroup = popupView.findViewById(com.example.exploro.R.id.radio_group);

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
            if (checkedId == com.example.exploro.R.id.radio_option1) {
                selectedOption = "kilometers (km)";
            } else if (checkedId == com.example.exploro.R.id.radio_option2) {
                selectedOption = "miles (mi)";
            }

            mDistanceReference.setValue(selectedOption);
        });

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showResetPasswordPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_reset_password, (ViewGroup) fragment.getView(), false);

        AccountViewModel accountViewModel = new ViewModelProvider(fragment).get(AccountViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final TextView emailTextView = popupView.findViewById(com.example.exploro.R.id.reset_email);

        accountViewModel.getEmailText().observe(fragment.getViewLifecycleOwner(), emailTextView::setText);

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showEditDisplayNamePopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_edit_name, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final EditText displayNameEditText = popupView.findViewById(com.example.exploro.R.id.display_name);
        final Button confirmButton = popupView.findViewById(com.example.exploro.R.id.confirm_button);
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
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_delete_account, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();

        final Button yesButton = popupView.findViewById(com.example.exploro.R.id.yes_button);
        final Button noButton = popupView.findViewById(com.example.exploro.R.id.no_button);
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

    public static void showAttractionDetailsPopup(Context context, View anchorView, PopupWindow.OnDismissListener dismissListener, String destinationID, String attractionID) {
        View popupView = LayoutInflater.from(context).inflate(com.example.exploro.R.layout.popup_planning_attraction_details, null, false);

        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);
        final TextView attractionNameTextView = popupView.findViewById(com.example.exploro.R.id.attraction_name);
        final TextView attractionDescriptionTextView = popupView.findViewById(com.example.exploro.R.id.attraction_description);
        final TextView attractionTimeTextView = popupView.findViewById(com.example.exploro.R.id.attraction_time);
        final TextView attractionAdultPriceTextView = popupView.findViewById(com.example.exploro.R.id.adult_price);
        final TextView attractionStudentPriceTextView = popupView.findViewById(com.example.exploro.R.id.student_price);
        final TextView attractionAddressTextView = popupView.findViewById(com.example.exploro.R.id.attraction_address);
        final Button closeButton = popupView.findViewById(com.example.exploro.R.id.close_button);
        final TextView attractionHoursTextView = popupView.findViewById(com.example.exploro.R.id.attraction_hours);

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mAttractionsReference = mDatabase.getReference("attractions/" + destinationID + "/" + attractionID);

        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String attractionName = dataSnapshot.child("name").getValue(String.class);
                    String attractionDescription = dataSnapshot.child("description").getValue(String.class);
                    String attractionTime = attractionTimeTextView.getText() + dataSnapshot.child("time").getValue(String.class);
                    String attractionAdultPrice = attractionAdultPriceTextView.getText() + dataSnapshot.child("prices/adult").getValue(String.class);
                    String attractionStudentPrice = attractionStudentPriceTextView.getText() + dataSnapshot.child("prices/student").getValue(String.class);
                    String attractionAddress = attractionAddressTextView.getText() + dataSnapshot.child("address").getValue(String.class);
                    String attractionHours = attractionHoursTextView.getText() + dataSnapshot.child("hours").getValue(String.class);

                    attractionNameTextView.setText(attractionName);
                    attractionDescriptionTextView.setText(attractionDescription);
                    attractionTimeTextView.setText(attractionTime);
                    attractionAdultPriceTextView.setText(attractionAdultPrice);
                    attractionStudentPriceTextView.setText(attractionStudentPrice);
                    attractionAddressTextView.setText(attractionAddress);
                    attractionHoursTextView.setText(attractionHours);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        closeButton.setOnClickListener(v -> popupWindow.dismiss());
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

package com.example.exploro.ui;

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

import java.util.concurrent.atomic.AtomicReference;

public class PopupMenu {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static FirebaseUser mUser;

    public static void showPopupCurrency(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.currency_popup, (ViewGroup) fragment.getView(), false);

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
                Log.w("LOGIN", "Failed to get database data.", error.toException());
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

    public static void showForgotPasswordPopup(LoginActivity activity, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(activity).inflate(R.layout.forgot_password_popup, null);

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

    public static void showResetPasswordPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.reset_password_popup, (ViewGroup) fragment.getView(), false);

        AccountViewModel accountViewModel = new ViewModelProvider(fragment).get(AccountViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final TextView emailTextView = popupView.findViewById(R.id.reset_email);

        accountViewModel.getEmailText().observe(fragment.getViewLifecycleOwner(), emailTextView::setText);

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showEditDisplayNamePopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.edit_display_name_popup, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final EditText displayNameEditText = popupView.findViewById(R.id.full_name);
        final Button confirmButton = popupView.findViewById(R.id.confirm_button);

        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        confirmButton.setOnClickListener(v -> {
            if (displayNameEditText.getText().toString().isEmpty()) {
                Toast.makeText(fragment.getContext(), "Please fill in name field!", Toast.LENGTH_SHORT).show();
                return;
            }
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayNameEditText.getText().toString())
                    .build();
            assert mUser != null;
            mUser.updateProfile(profileUpdates);
            Toast.makeText(fragment.getContext(), "Display name updated!", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }

    public static boolean showDeleteAccountPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.delete_account_popup, (ViewGroup) fragment.getView(), false);

        AtomicReference<Boolean> checkDelete = new AtomicReference<>(false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final Button yesButton = popupView.findViewById(R.id.yes_button);
        final Button noButton = popupView.findViewById(R.id.no_button);

        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        noButton.setOnClickListener(v -> popupWindow.dismiss());

        yesButton.setOnClickListener(v -> {
            assert mUser != null;
            mUser.delete();
            Toast.makeText(fragment.getContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            mAuth.signOut();
            checkDelete.set(true);
        });

        return checkDelete.get();
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

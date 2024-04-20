package com.example.exploro.ui;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.exploro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class PopupMenu {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mCurrencyRef;
    private static FirebaseUser mUser;

    public static void showPopupCurrency(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.currency_popup, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        assert mUser != null;
        mCurrencyRef = mDatabase.getReference("users/" + mUser.getUid() + "/currency/");

        RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);

        mCurrencyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                mCurrencyRef.removeEventListener(this);
                String currencyValue = dataSnapshot.getValue(String.class);

                if (currencyValue != null) {
                    switch (currencyValue) {
                        case "RON":
                            radioGroup.check(R.id.radio_option1);
                            break;
                        case "EUR":
                            radioGroup.check(R.id.radio_option2);
                            break;
                        case "USD":
                            radioGroup.check(R.id.radio_option3);
                            break;
                    }
                }

                mCurrencyRef.addValueEventListener(this);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
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

            mCurrencyRef.setValue(selectedOption);
        });

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        Drawable drawable = ContextCompat.getDrawable(fragment.requireContext(), R.drawable.popup_background);
        popupWindow.setBackgroundDrawable(drawable);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        if (dismissListener != null) {
            popupWindow.setOnDismissListener(dismissListener);
        }
    }
}
package com.example.exploro.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import androidx.fragment.app.Fragment;
import com.example.exploro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PopupMenu {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    public static void showPopupCurrency(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.currency_popup, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        assert user != null;
        DatabaseReference usersRef = mDatabase.getReference("users/" + user.getUid() + "/currency");

        RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_option1) {
                usersRef.setValue("RON");
            } else if (checkedId == R.id.radio_option2) {
                usersRef.setValue("EUR");
            } else if (checkedId == R.id.radio_option3) {
                usersRef.setValue("USD");
            }
        });

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
    }

    public static void showPopupDistance(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.currency_popup, (ViewGroup) fragment.getView(), false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        assert user != null;
        DatabaseReference usersRef = mDatabase.getReference("users/" + user.getUid() + "/distance");

        RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_option1) {
                usersRef.setValue("RON");
            } else if (checkedId == R.id.radio_option2) {
                usersRef.setValue("EUR");
            } else if (checkedId == R.id.radio_option3) {
                usersRef.setValue("USD");
            }
        });

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
    }
}


package com.example.exploro.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;

import com.example.exploro.R;
import com.example.exploro.domain.UserManager;
import com.example.exploro.domain.AttractionManager;
import com.example.exploro.domain.TripManager;
import com.example.exploro.models.Trip;
import com.example.exploro.ui.activities.LoginActivity;
import com.example.exploro.ui.adapters.HomeSavedTripsAdapter;

public class PopupMenu {

    public static void showForgotPasswordPopup(LoginActivity activity, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(activity).inflate(com.example.exploro.R.layout.popup_login_forgot_password, (ViewGroup) activity.getWindow().getDecorView(), false);

        final EditText emailEditText = popupView.findViewById(com.example.exploro.R.id.email);
        final Button resetPasswordButton = popupView.findViewById(com.example.exploro.R.id.reset_password);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        resetPasswordButton.setOnClickListener(v -> UserManager.forgotPassword(emailEditText.getText().toString(), activity, popupWindow));
    }

    public static void showResetPasswordPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener, String email) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_reset_password, (ViewGroup) fragment.getView(), false);

        final TextView emailTextView = popupView.findViewById(com.example.exploro.R.id.reset_email);
        String emailText = "Reset email password sent to " + email;

        emailTextView.setText(emailText);

        configurePopupWindow(anchorView, dismissListener, popupView);
    }

    public static void showEditDisplayNamePopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_edit_name, (ViewGroup) fragment.getView(), false);

        final EditText displayNameEditText = popupView.findViewById(com.example.exploro.R.id.display_name);
        final Button confirmButton = popupView.findViewById(com.example.exploro.R.id.confirm_button);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        confirmButton.setOnClickListener(v -> UserManager.editDisplayName(displayNameEditText.getText().toString(), fragment, popupWindow));
    }

    public static void showDeleteAccountPopup(Fragment fragment, View anchorView, PopupWindow.OnDismissListener dismissListener) {
        View popupView = LayoutInflater.from(fragment.getContext()).inflate(com.example.exploro.R.layout.popup_account_delete_account, (ViewGroup) fragment.getView(), false);

        final Button yesButton = popupView.findViewById(com.example.exploro.R.id.yes_button);
        final Button noButton = popupView.findViewById(com.example.exploro.R.id.no_button);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        yesButton.setOnClickListener(v -> {
            UserManager.deleteAccount(fragment);
            popupWindow.dismiss();
        });

        noButton.setOnClickListener(v -> popupWindow.dismiss());
    }

    public static void showAttractionDetailsPopup(Context context, View anchorView, PopupWindow.OnDismissListener dismissListener, String destinationID, String attractionID) {
        View popupView = LayoutInflater.from(context).inflate(com.example.exploro.R.layout.popup_planning_attraction_details, null, false);

        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);
        final TextView nameTextView = popupView.findViewById(com.example.exploro.R.id.attraction_name);
        final TextView descriptionTextView = popupView.findViewById(com.example.exploro.R.id.attraction_description);
        final TextView timeSpentTextView = popupView.findViewById(com.example.exploro.R.id.attraction_time);
        final TextView attractionAdultPriceTextView = popupView.findViewById(com.example.exploro.R.id.adult_price);
        final TextView studentPriceTextView = popupView.findViewById(com.example.exploro.R.id.student_price);
        final TextView addressTextView = popupView.findViewById(com.example.exploro.R.id.attraction_address);
        final TextView linkTextView = popupView.findViewById(com.example.exploro.R.id.attraction_website);
        final TextView hoursTextView = popupView.findViewById(com.example.exploro.R.id.attraction_hours);
        final Button closeButton = popupView.findViewById(com.example.exploro.R.id.close_button);

        AttractionManager.fetchAttractionPlanningDetails(destinationID, attractionID, nameTextView,
                                                        descriptionTextView, timeSpentTextView,
                                                        attractionAdultPriceTextView, studentPriceTextView,
                                                        addressTextView, hoursTextView, linkTextView);

        closeButton.setOnClickListener(v -> popupWindow.dismiss());
    }

    public static void showDeleteTripPopup(Context context, View anchorView, PopupWindow.OnDismissListener dismissListener, Trip trip, HomeSavedTripsAdapter adapter, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_home_delete_trip, null, false);

        final Button yesButton = popupView.findViewById(com.example.exploro.R.id.yes_button);
        final Button noButton = popupView.findViewById(com.example.exploro.R.id.no_button);
        PopupWindow popupWindow = configurePopupWindow(anchorView, dismissListener, popupView);

        yesButton.setOnClickListener(v -> {
            TripManager.deleteTrip(trip.getTripID(), position, adapter);
            popupWindow.dismiss();
        });

        noButton.setOnClickListener(v -> popupWindow.dismiss());
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
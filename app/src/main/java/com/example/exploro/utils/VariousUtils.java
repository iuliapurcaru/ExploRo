package com.example.exploro.utils;

import android.util.Patterns;
import com.google.firebase.database.DataSnapshot;

import java.util.regex.Pattern;

public class VariousUtils {


    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        final Pattern textPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\d).+$");
        return textPattern.matcher(password).matches() && password.length() >= 8;
    }

    public static <T> T getValueOrDefault(DataSnapshot snapshot, Class<T> clazz, T defaultValue) {
        T value = snapshot.getValue(clazz);
        return (value != null) ? value : defaultValue;
    }

}

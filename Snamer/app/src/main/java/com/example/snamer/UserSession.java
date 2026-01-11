package com.example.snamer;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF = "snamer_user_pref";

    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password"; // lokal demo (bukan best practice)

    public static boolean isLoggedIn(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGGED_IN, false);
    }

    public static void setLoggedIn(Context ctx, boolean value) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }

    public static void saveUser(Context ctx, String name, String phone, String email, String password) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_PHONE, phone)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public static String getName(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_NAME, "");
    }

    public static String getPhone(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_PHONE, "");
    }

    public static String getEmail(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_EMAIL, "");
    }

    public static boolean checkLogin(Context ctx, String phone, String password) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String savedPhone = sp.getString(KEY_PHONE, "");
        String savedPass = sp.getString(KEY_PASSWORD, "");
        return savedPhone.equals(phone) && savedPass.equals(password);
    }

    public static void logout(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .remove(KEY_NAME)
                .remove(KEY_PHONE)
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .apply();
    }
    public static boolean hasRegisteredAccount(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String phone = sp.getString(KEY_PHONE, "");
        String pass  = sp.getString(KEY_PASSWORD, "");
        return !phone.isEmpty() && !pass.isEmpty();
    }

}

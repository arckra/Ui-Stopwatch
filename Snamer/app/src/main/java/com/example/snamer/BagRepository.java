package com.example.snamer;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BagRepository {

    private static final String PREF = "snamer_bag_pref";
    private static final String KEY = "bag_items";

    public static ArrayList<BagItem> load(Context ctx) {
        ArrayList<BagItem> items = new ArrayList<>();
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "[]");

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                items.add(new BagItem(
                        o.optString("id"),
                        o.optString("name")
                ));
            }
        } catch (Exception ignored) { }

        return items;
    }

    public static void save(Context ctx, ArrayList<BagItem> items) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        try {
            for (BagItem it : items) {
                JSONObject o = new JSONObject();
                o.put("id", it.id);
                o.put("name", it.name);
                arr.put(o);
            }
        } catch (Exception ignored) { }

        sp.edit().putString(KEY, arr.toString()).apply();
    }

    public static void add(Context ctx, BagItem item) {
        ArrayList<BagItem> items = load(ctx);
        items.add(item);
        save(ctx, items);
    }

    public static void clear(Context ctx) {
        save(ctx, new ArrayList<>());
    }
}

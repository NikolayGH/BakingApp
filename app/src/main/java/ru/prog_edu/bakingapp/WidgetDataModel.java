package ru.prog_edu.bakingapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

class WidgetDataModel {
    private static final String PREF_KEY_INGREDIENTS = "ingredientData";

    public static Set<String> getDataFromSharedPrefs(Context context) {
        Set<String> list = new HashSet<>();
        if(list.isEmpty()) {
            SharedPreferences sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
            list = sharedPref.getStringSet(PREF_KEY_INGREDIENTS, list);
        }
        return list;
    }
}

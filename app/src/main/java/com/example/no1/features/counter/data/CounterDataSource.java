package com.example.no1.features.counter.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.no1.features.counter.models.CounterModel;

public class CounterDataSource {
    private static final String PREF_NAME = "counter_prefs";
    private static final String KEY_COUNT = "counter_count";
    private static final String KEY_LAST_UPDATE = "counter_last_update";

    private SharedPreferences sharedPreferences;
    private static CounterDataSource instance;

    private CounterDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized CounterDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CounterDataSource(context);
        }
        return instance;
    }

    public void saveCounter(CounterModel counter) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_COUNT, counter.getCount());
        editor.putLong(KEY_LAST_UPDATE, counter.getLastUpdateTime());
        editor.apply();
    }

    public CounterModel loadCounter() {
        int count = sharedPreferences.getInt(KEY_COUNT, 0);
        long lastUpdate = sharedPreferences.getLong(KEY_LAST_UPDATE, System.currentTimeMillis());
        return new CounterModel(count, lastUpdate);
    }

    public void clearCounter() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_COUNT);
        editor.remove(KEY_LAST_UPDATE);
        editor.apply();
    }
}
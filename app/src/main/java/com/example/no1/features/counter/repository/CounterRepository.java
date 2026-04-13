package com.example.no1.features.counter.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.counter.data.CounterDataSource;
import com.example.no1.features.counter.models.CounterModel;

public class CounterRepository {
    private static CounterRepository instance;
    private CounterDataSource dataSource;
    private MutableLiveData<CounterModel> counterLiveData;

    private CounterRepository(Context context) {
        dataSource = CounterDataSource.getInstance(context);
        counterLiveData = new MutableLiveData<>();
        CounterModel savedCounter = dataSource.loadCounter();
        counterLiveData.setValue(savedCounter);
    }

    public static synchronized CounterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CounterRepository(context);
        }
        return instance;
    }

    public LiveData<CounterModel> getCounter() {
        return counterLiveData;
    }

    public void increment() {
        CounterModel current = counterLiveData.getValue();
        if (current != null) {
            current.increment();
            counterLiveData.setValue(current);
            dataSource.saveCounter(current);
        }
    }

    public void reset() {
        CounterModel current = counterLiveData.getValue();
        if (current != null) {
            current.reset();
            counterLiveData.setValue(current);
            dataSource.saveCounter(current);
        }
    }
}
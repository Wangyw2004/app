package com.example.no1.features.counter.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.no1.features.counter.models.CounterModel;
import com.example.no1.features.counter.repository.CounterRepository;

public class CounterViewModel extends AndroidViewModel {

    private CounterRepository repository;
    private LiveData<CounterModel> counter;

    public CounterViewModel(Application application) {
        super(application);
        repository = CounterRepository.getInstance(application);
        counter = repository.getCounter();
    }

    public LiveData<CounterModel> getCounter() {
        return counter;
    }

    public void increment() {
        repository.increment();
    }

    public void reset() {
        repository.reset();
    }
}
package com.example.no1.features.service.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.repository.ServiceRepository;
import java.util.List;

public class ServiceViewModel extends AndroidViewModel {

    private ServiceRepository repository;
    private LiveData<List<Service>> services;
    private LiveData<Boolean> isLoading;
    private LiveData<String> errorMessage;

    public ServiceViewModel(Application application) {
        super(application);
        repository = ServiceRepository.getInstance(application);
        services = repository.getServices();
        isLoading = repository.getIsLoading();
        errorMessage = repository.getErrorMessage();
    }

    public LiveData<List<Service>> getServices() {
        return services;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadUserServices(String userId) {
        repository.loadUserServices(userId);
    }

    public void loadAllServices() {
        repository.loadAllServices();
    }

    public void submitComplaint(Service service) {
        repository.submitComplaint(service);
    }

    public void submitRepair(Service service) {
        repository.submitRepair(service);
    }

    public void resetErrorMessage() {
        repository.resetErrorMessage();
    }
}
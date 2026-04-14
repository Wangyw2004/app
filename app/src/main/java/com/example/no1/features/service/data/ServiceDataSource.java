package com.example.no1.features.service.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.service.models.Service;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ServiceDataSource {
    private static final String PREF_NAME = "service_prefs";
    private static final String KEY_SERVICES = "services_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static ServiceDataSource instance;

    private ServiceDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized ServiceDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceDataSource(context);
        }
        return instance;
    }

    public void saveServices(List<Service> services) {
        String json = gson.toJson(services);
        sharedPreferences.edit().putString(KEY_SERVICES, json).apply();
    }

    public List<Service> loadServices() {
        String json = sharedPreferences.getString(KEY_SERVICES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Service>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addService(Service service) {
        List<Service> services = loadServices();
        services.add(0, service);
        saveServices(services);
    }

    public void updateService(Service service) {
        List<Service> services = loadServices();
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(service.getId())) {
                services.set(i, service);
                break;
            }
        }
        saveServices(services);
    }

    public Service getServiceById(String id) {
        List<Service> services = loadServices();
        for (Service service : services) {
            if (service.getId().equals(id)) {
                return service;
            }
        }
        return null;
    }

    public List<Service> getServicesByUserId(String userId) {
        List<Service> services = loadServices();
        List<Service> result = new ArrayList<>();
        for (Service service : services) {
            if (service.getUserId().equals(userId)) {
                result.add(service);
            }
        }
        return result;
    }

    public List<Service> getAllServices() {
        return loadServices();
    }
}